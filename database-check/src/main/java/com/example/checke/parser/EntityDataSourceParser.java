package com.example.checke.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.internal.asm.ClassReader;
import org.modelmapper.internal.asm.Opcodes;
import org.modelmapper.internal.asm.tree.AnnotationNode;
import org.modelmapper.internal.asm.tree.ClassNode;
import org.modelmapper.internal.asm.tree.FieldNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import com.example.checke.bean.EntityDataSource;
import com.example.commons.core.exceptions.ServerException;
import com.example.commons.core.utils.StringUtils;
import com.example.commons.core.utils.TypeUtils;
import com.example.properties.CheckEntitiesProperties;
import com.google.common.base.CaseFormat;

import aj.org.objectweb.asm.Type;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * <p>
 * 代码实体解析器
 * <p>
 *
 * @author : 21
 * @since : 2024/3/14 16:19
 */

public class EntityDataSourceParser {

    private final CheckEntitiesProperties checkEntitiesProperties;
    private final ResourceParser resourceParser;
    private Map<String, ClassNode> classNodeMap = new HashMap<>();

    public EntityDataSourceParser(CheckEntitiesProperties checkEntitiesProperties) {
        this.checkEntitiesProperties = checkEntitiesProperties;
        this.resourceParser = new ResourceParser(checkEntitiesProperties);
    }

    /**
     * 解析实体
     *
     * @return
     */
    public List<EntityDataSource> parserEntities() {
        return resourceParser.getResources()
                .stream()
                .map(this::getClassNode)
                .map(classNode -> {
                    if (isAbstractClass(classNode) || shouldExcludeClass(classNode) || isInnerClass(classNode)) {
                        return null;
                    }
                    return new EntityDataSource()
                            .setTableName(parseTableName(classNode))
                            .setColumnNames(parseColumnNames(classNode))
                            .setDbType(parseDbType(classNode))
                            .setServiceName(parseServiceName(classNode));
                })
                .filter(Objects::nonNull)
                .filter(entityDataSource -> !CollectionUtils.isEmpty(entityDataSource.getColumnNames()))
                .collect(Collectors.toList());
    }

    /**
     * 根据路径解析出来的类文件，获取类信息
     *
     * @param resource
     * @return
     */
    public ClassNode getClassNode(Resource resource) {
        try {
            //使用ClassReader来读取从Resource对象获取的类文件  可以在不实例化类文件本身的情况下直接从字节码中读取信息
            ClassReader reader = new ClassReader(resource.getInputStream());
            //ClassNode 对象存储了类的结构信息，例如类的名称、方法、字段、注解等元信息
            ClassNode classNode = new ClassNode();
            //0表示默认行为，不需要任何特殊解析或忽略
            reader.accept(classNode, 0);
            classNodeMap.put(classNode.name, classNode);
            return classNode;
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    /**
     * 检查该类 是否为抽象类或者接口
     *
     * @param classNode
     * @return
     */
    private boolean isAbstractClass(ClassNode classNode) {
        return (classNode.access & Opcodes.ACC_ABSTRACT) != 0
                || (classNode.access & Opcodes.ACC_INTERFACE) != 0;
    }

    /**
     * 检查该类 是否需要跳过检查
     *
     * @param classNode
     * @return
     */
    private boolean shouldExcludeClass(ClassNode classNode) {
        return checkEntitiesProperties.getExcludeClasses().contains(classNode.name.replace(StringUtils.SLASH, StringUtils.DOT));
    }

    /**
     * 检查该类 是否未内部类
     *
     * @param classNode
     * @return
     */
    private boolean isInnerClass(ClassNode classNode) {
        return StringUtils.isNotBlank(classNode.nestHostClass);
    }

    /**
     * 根据classNode解析表名
     *
     * @param classNode
     * @return
     */
    private String parseTableName(ClassNode classNode) {
        Optional<AnnotationNode> annotationNode = filterTableNameAnnotation(classNode);
        return StringUtils.removeBackQuote(annotationNode.isPresent()
                ? parseTableNameFromAnnotation(classNode, annotationNode.get())
                : convertToUnderscoreCase(parseClassSimpleName(classNode)));
    }

    /**
     * 根据classNode解析字段名
     *
     * @param classNode
     * @return
     */
    private List<String> parseColumnNames(ClassNode classNode) {
        List<String> fields = classNode.fields.stream()
                .map(fieldNode -> {
                    if ((fieldNode.access & Opcodes.ACC_STATIC) != 0) {
                        return null;
                    }
                    Optional<AnnotationNode> annotationNode = filterFieldNameAnnotation(fieldNode);
                    return annotationNode.isPresent() ? parseColumnNameFromAnnotation(fieldNode, annotationNode.get())
                            : convertToUnderscoreCase(fieldNode.name);
                }).filter(Objects::nonNull)
                .map(StringUtils::removeBackQuote)
                .collect(Collectors.toList());
        String superName = classNode.superName;
        //如果存在父类且不是Object  递归获取父类字段填充到子类
        if (StringUtils.isNotBlank(superName) && !Objects.equals(superName, "java/lang/Object")) {
            if (!classNodeMap.containsKey(superName)) {
                Resource superClassResrouce = new ClassPathResource((superName + ".class"));
                classNodeMap.put(superName, getClassNode(superClassResrouce));
            }
            ClassNode superClassNode = classNodeMap.get(superName);
            fields.addAll(parseColumnNames(superClassNode));
        }
        return fields;
    }

    /**
     * 根据classNode解析数据库类型
     *
     * @param classNode
     * @return
     */
    private String parseDbType(ClassNode classNode) {
        String partAfterEntity = "";
        String[] split = classNode.name.split(StringUtils.SLASH);
        for (int i = 0; i < split.length; i++) {
            if ("entity".equals(split[i])) {
                partAfterEntity = split[i + 1];
                break;
            }
        }
        return checkEntitiesProperties.getDbTypes().contains(partAfterEntity) ? partAfterEntity : checkEntitiesProperties.getDefaultDbType();
    }

    /**
     * 根据classNode解析服务名称
     *
     * @param classNode
     * @return
     */
    private String parseServiceName(ClassNode classNode) {
        return classNode.name.split(StringUtils.SLASH)[3];
    }

    /**
     * 根据classNode获取TableName注解
     *
     * @param classNode
     * @return
     */
    private Optional<AnnotationNode> filterTableNameAnnotation(ClassNode classNode) {
        if (CollectionUtils.isEmpty(classNode.visibleAnnotations)) {
            return Optional.empty();
        }
        return classNode.visibleAnnotations.stream()
                //desc是是一个描述符  表示注解类型的内部形式  TableName -> com/baomidou/mybatisplus/annotation/TableName
                //getType接受一个描述符字符串作为输入，并返回相应的 Type 对象实例
                .filter(annotationNode -> Objects.equals(Type.getType(annotationNode.desc).getClassName(), "com.baomidou.mybatisplus.annotation.TableName"))
                .findFirst();
    }

    /**
     * 根据fieldNode获取TableField注解
     *
     * @param fieldNode
     * @return
     */
    private Optional<AnnotationNode> filterFieldNameAnnotation(FieldNode fieldNode) {
        if (CollectionUtils.isEmpty(fieldNode.visibleAnnotations)) {
            return Optional.empty();
        }
        return fieldNode.visibleAnnotations.stream()
                .filter(annotationNode -> Objects.equals(Type.getType(annotationNode.desc).getClassName(), "com.baomidou.mybatisplus.annotation.TableField"))
                .findFirst();
    }

    /**
     * 根据@TableName注解获取tableName，不存在则根据类名获取
     *
     * @param classNode
     * @param classAnnotation
     * @return
     */
    private String parseTableNameFromAnnotation(ClassNode classNode, AnnotationNode classAnnotation) {
        return Optional
                //防止出现有注解，但是注解没写值的情况
                .ofNullable(getAnnotationValue(classAnnotation, "value"))
                .orElse(convertToUnderscoreCase(parseClassSimpleName(classNode)));
    }

    /**
     * 根据@TableField注解获取字段名
     *
     * @param fieldNode
     * @param filedAnnotation
     * @return
     */
    private String parseColumnNameFromAnnotation(FieldNode fieldNode, AnnotationNode filedAnnotation) {
        //如果没有排除那就是数据库字段
        return TypeUtils.castToBoolean(getAnnotationValue(filedAnnotation, "exist"), true) ?
                Optional.ofNullable(getAnnotationValue(filedAnnotation, "value")).orElse(convertToUnderscoreCase(fieldNode.name)) : null;
    }

    /**
     * 根据attr 获取注解值
     *
     * @param annotationNode
     * @param attr
     * @return
     */
    private String getAnnotationValue(AnnotationNode annotationNode, String attr) {
        if (Objects.isNull(annotationNode.values)) {
            return null;
        }
        int valueIndex = annotationNode.values.indexOf(attr);
        if (valueIndex == -1) {
            return null;
        }
        return annotationNode.values.get(valueIndex + 1).toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param name
     * @return
     */
    private String convertToUnderscoreCase(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    /**
     * 根据classNodeName获取类名
     *
     * @param classNode
     * @return
     */
    private String parseClassSimpleName(ClassNode classNode) {
        return CharSequenceUtil.subAfter(classNode.name, StringUtils.SLASH, true);
    }
}
