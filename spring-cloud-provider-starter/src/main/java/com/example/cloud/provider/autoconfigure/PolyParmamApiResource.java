package com.example.cloud.provider.autoconfigure;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webmvc.core.SpringWebMvcProvider;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.example.cloud.provider.openapi3.annotaions.PolyParam;
import com.example.cloud.provider.openapi3.annotaions.PolyParams;

import cn.hutool.core.util.ReflectUtil;

public class PolyParmamApiResource extends SpringWebMvcProvider {

    @Override
    public String findPathPrefix(SpringDocConfigProperties springDocConfigProperties) {
        Map<RequestMappingInfo, HandlerMethod> map = getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            Set<String> patterns = getActivePatterns(requestMappingInfo);
            if (!CollectionUtils.isEmpty(patterns)) {
                for (String operationPath : patterns) {
                    if (operationPath.endsWith(springDocConfigProperties.getApiDocs().getPath()))
                        return operationPath.replace(springDocConfigProperties.getApiDocs().getPath(), StringUtils.EMPTY);
                }
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Gets active patterns.
     *
     * @param requestMapping the request mapping info
     * @return the active patterns
     */
    public Set<String> getActivePatterns(Object requestMapping) {
        Set<String> patterns = null;
        RequestMappingInfo requestMappingInfo = (RequestMappingInfo) requestMapping;
        PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
        if (patternsRequestCondition != null)
            patterns = patternsRequestCondition.getPatterns();
        else {
            PathPatternsRequestCondition pathPatternsRequestCondition = requestMappingInfo.getPathPatternsCondition();
            if (pathPatternsRequestCondition != null)
                patterns = pathPatternsRequestCondition.getPatternValues();
        }
        return patterns;
    }

    @Override
    public Map getHandlerMethods() {
        //TODO 这里还有待优化（只做了返回参数多态）
        if (this.handlerMethods == null) {
            Map<String, RequestMappingHandlerMapping> beansOfTypeRequestMappingHandlerMapping = applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);
            //这里存放多态接口信息
            LinkedHashMap<RequestMappingInfo, HandlerMethod> polyHandlerMethods = new LinkedHashMap<>();
            beansOfTypeRequestMappingHandlerMapping.values().stream()
                    .flatMap(mapping -> mapping.getHandlerMethods().entrySet().stream())
                    //过滤出需要处理的接口
                    .filter(entites -> Objects.nonNull(entites.getValue().getMethod().getAnnotation(PostMapping.class)) || Objects.nonNull(entites.getValue().getMethod().getAnnotation(PutMapping.class)))
                    .filter(entites -> Objects.nonNull(entites.getValue().getMethod().getAnnotation(PolyParam.class)) || Objects.nonNull(entites.getValue().getMethod().getAnnotation(PolyParams.class)))
                    .forEach(entity -> {
                        HandlerMethod oldHandlerMethod = entity.getValue();
                        RequestMappingInfo oldMappingInfo = entity.getKey();
                        //对旧参数进行拆分调整
                        Method method = oldHandlerMethod.getMethod();
                        oldMappingInfo.getPathPatternsCondition().getPatterns().stream().findFirst().ifPresent(oldPath -> {
                            PolyParams polyParams = entity.getValue().getMethod().getAnnotation(PolyParams.class);
                            for (PolyParam polyParam : polyParams.value()) {
                                //根据注解定义新路径
                                StringBuilder newKeyName = new StringBuilder(oldPath.getPatternString());
                                newKeyName.append("?")
                                        .append(polyParams.property())
                                        .append("=")
                                        .append(polyParam.type());
                                //根据注解定义新的返回类型
                                ReflectUtil.setFieldValue(method, ReflectUtil.getField(method.getClass(), "returnType"), polyParam.resultType());
                                //构建新的mapping
                                RequestMappingInfo newMappingInfo = RequestMappingInfo
                                        .paths(newKeyName.toString())
                                        .methods(oldMappingInfo.getMethodsCondition().getMethods().toArray(new RequestMethod[]{}))
                                        .build();
                                polyHandlerMethods.put(newMappingInfo, new HandlerMethod(oldHandlerMethod.getBean().toString(), applicationContext, method));
                            }
                        });
                    });
            LinkedHashMap<RequestMappingInfo, HandlerMethod> result = beansOfTypeRequestMappingHandlerMapping.values().stream()
                    .map(AbstractHandlerMethodMapping::getHandlerMethods)
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a1, a2) -> a1, LinkedHashMap::new));
            result.putAll(polyHandlerMethods);
            this.handlerMethods = result;
        }
        return this.handlerMethods;
    }

}
