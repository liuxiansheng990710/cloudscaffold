package com.example.mybatisplus.enhance.injector;

import static java.util.stream.Collectors.toList;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.injector.methods.DeleteBatchByIds;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.baomidou.mybatisplus.core.injector.methods.DeleteByMap;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.SelectBatchByIds;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.SelectByMap;
import com.baomidou.mybatisplus.core.injector.methods.SelectCount;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.baomidou.mybatisplus.core.injector.methods.SelectMaps;
import com.baomidou.mybatisplus.core.injector.methods.SelectMapsPage;
import com.baomidou.mybatisplus.core.injector.methods.SelectObjs;
import com.baomidou.mybatisplus.core.injector.methods.SelectPage;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.example.mybatisplus.enhance.injector.instances.DataBaseEngine;
import com.example.mybatisplus.enhance.injector.instances.MySQLEngine;
import com.example.mybatisplus.enhance.injector.instances.PostgreSQLEngine;

import cn.hutool.core.collection.CollUtil;

/**
 * <p>
 * 数据库字段检测拦截器（基于mybatis-plus）
 * 主要作用：检测代码表字段与数据库字段是否一致
 * 例：
 * 1. 代码表中字段为A,B,C 数据库字段为A,B 数据库字段缺少C  项目启动时会抛出异常
 * 2. 代码表中字段为A,B 数据库字段为A,B,C  代码字段缺少C  项目启动时不会抛出异常
 * 3. 代码表中字段为A,B,C 数据库字段为A,B,C 字段一致  项目启动时不会抛出异常
 * </p>
 * <p>
 * {@link com.baomidou.mybatisplus.core.injector.AbstractSqlInjector)
 * 一般来说这个方法是实现自定义SQL方法 在Mapper(每一个继承BaseMapper的Mapper)初始化的时候会调用 -> 将BaseMapper中的方法(根据每个Mapper信息)进行SQL预处理
 * 所以在初始化完Mapper以后，根据Mapper获取数据库信息 进行数据库字段检测
 * </P>
 *
 * @author : 21
 * @since : 2024/2/4 11:32
 */

public class ColumnsCheckInjector extends AbstractSqlInjector {

    protected static final Log log = LogFactory.getLog(ColumnsCheckInjector.class);

    protected static final Map<DbType, DataBaseEngine> DATA_BASE_ENGINE_MAP = new EnumMap<>(DbType.class);

    static {
        //目前只支持MySQL PostgreSQL
        DATA_BASE_ENGINE_MAP.put(DbType.MYSQL, MySQLEngine.instance);
        DATA_BASE_ENGINE_MAP.put(DbType.POSTGRE_SQL, PostgreSQLEngine.instance);
    }

    /**
     * 添加自定义数据库检测拦截器
     *
     * @param dbType
     * @param dataBaseEngine
     */
    public void addInjector(DbType dbType, DataBaseEngine dataBaseEngine) {
        DATA_BASE_ENGINE_MAP.put(dbType, dataBaseEngine);
    }

    /**
     * 因为使用时，@TableId()注解 可能不需要，直接使用id字段进行映射为主键使用
     * 所以不判断是否存在pk（具体使用时需要注意 若无@TableId住家时，需要存在id字段）
     * {@link com.baomidou.mybatisplus.core.injector.DefaultSqlInjector#getMethodList}
     *
     * @param mapperClass
     * @param tableInfo
     * @return
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        Stream.Builder<AbstractMethod> builder = Stream.<AbstractMethod>builder()
                .add(new Insert())
                .add(new Delete())
                .add(new DeleteByMap())
                .add(new Update())
                .add(new SelectByMap())
                .add(new SelectCount())
                .add(new SelectMaps())
                .add(new SelectMapsPage())
                .add(new SelectObjs())
                .add(new SelectList())
                .add(new SelectPage())
                .add(new DeleteById())
                .add(new DeleteBatchByIds())
                .add(new UpdateById())
                .add(new SelectById())
                .add(new SelectBatchByIds());
        return builder.build().collect(toList());
    }

    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        super.inspectInject(builderAssistant, mapperClass);
        //初始化完Mapper以后进行数据库检测
        checkTableStructure(builderAssistant, mapperClass);
    }

    /**
     * 检测数据库表结构
     *
     * @param builderAssistant
     * @param mapperClass
     */
    private void checkTableStructure(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        //根据反射 获取实现Mapper接口的实际实现类(也就是获取每个继承BaseMapper的业务Mapper)
        Class<?> modelClass = ReflectionKit.getSuperClassGenericType(mapperClass, Mapper.class, 0);
        //获取表信息
        TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
        String tableName = tableInfo.getTableName();
        //获取MyBatis当前运行环境的代理数据源
        DataSource proxyDataSource = builderAssistant.getConfiguration().getEnvironment().getDataSource();
        //如果是动态数据源 获取所有数据源类型
        List<DataSource> dataSources = isDynamicDataSource(proxyDataSource) ? getOriginDataSources(proxyDataSource) : Collections.singletonList(proxyDataSource);
        List<String> databaseColumns;
        List<String> modelColumns;
        Collection<String> dataBaseMissingfields = Collections.emptyList();
        for (DataSource dataSource : dataSources) {
            //使用 try-with-resources 在任务完成后 JVM会自动关闭try中打开的资源
            try (Connection connection = dataSource.getConnection()) {
                DbType dbType = JdbcUtils.getDbType(connection.getMetaData().getURL());
                DataBaseEngine engine = DATA_BASE_ENGINE_MAP.get(dbType);
                //如果没有该类型执行器 则跳出此次循环
                if (Objects.isNull(engine)) {
                    log.warn(String.format("The database type [%s] does not support structure checking. tablename is [%s]", dbType.getDb(), tableName));
                } else {
                    databaseColumns = engine.getDataBaseColumns(connection, tableName);
                    modelColumns = engine.getModelColumns(tableInfo);
                    dataBaseMissingfields = CollUtil.subtract(modelColumns, databaseColumns);
                    //如果已经获取到数据库字段,那说明已经找到了当前环境实际运行的数据源,所以跳出循环
                    if (CollectionUtils.isNotEmpty(databaseColumns)) {
                        break;
                    }
                }

            } catch (Exception ignore) {
                //一般情况下 获取数据源链接不会有问题
            }
        }
        //对于缺失字段 抛出异常
        if (CollectionUtils.isNotEmpty(dataBaseMissingfields)) {
            String errorMessage = String.format("The database table [%s] is missing fields, please check the database structure. Missing field: %s", tableName, dataBaseMissingfields);
            log.error(errorMessage);
            throw new MybatisPlusException(errorMessage);
        }
    }

    /**
     * 根据代理数据源 获取原始数据源(当使用动态数据源时，这里会存在多个数据源)
     *
     * @param proxyDataSource
     * @return
     */
    private List<DataSource> getOriginDataSources(DataSource proxyDataSource) {
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) proxyDataSource;
        //获取动态数据源
        return new ArrayList<>(dynamicRoutingDataSource.getDataSources().values());
    }

    /**
     * 是否使用动态数据源
     * 如果添加了dynamic-datasource-spring-boot-starter(动态数据源库)
     * 那就可以找到DynamicRoutingDataSource这个类
     *
     * @return
     */
    private boolean isDynamicDataSource(DataSource proxyDataSource) {
        try {
            //如果找到这个类 并且代理数据源类型是动态数据源那就说明使用了动态数据源
            Class.forName("com.baomidou.dynamic.datasource.DynamicRoutingDataSource");
            if (proxyDataSource instanceof DynamicRoutingDataSource) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
