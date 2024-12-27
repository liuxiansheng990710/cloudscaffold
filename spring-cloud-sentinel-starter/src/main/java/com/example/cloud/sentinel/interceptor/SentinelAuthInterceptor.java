package com.example.cloud.sentinel.interceptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.example.cloud.sentinel.config.properties.AuthOriginConfiguration;

/**
 * <p>
 * Sentinel鉴权拦截器
 * 作用：拦截所有请求，对请求中是否包含自定义鉴权规则
 * 及是否鉴权通过做限制
 * <p>
 *
 * @author : 21
 * @since : 2023/8/21 10:22
 */

public class SentinelAuthInterceptor extends SentinelWebInterceptor {

    @Autowired
    private AuthOriginConfiguration configuration;

    private final SentinelWebMvcConfig config;

    public SentinelAuthInterceptor() {
        this(new SentinelWebMvcConfig());
    }

    public SentinelAuthInterceptor(SentinelWebMvcConfig config) {
        super(config);
        this.config = config;
    }

    /**
     * {@link com.alibaba.csp.sentinel.adapter.spring.webmvc.AbstractSentinelInterceptor#preHandle}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currentResourceName = request.getRequestURI();
        try {

            String parseOrigin = parseOrigin(request);
            if (StringUtil.isBlank(currentResourceName) || StringUtil.isBlank(parseOrigin)) {
                throw new AuthorityException(parseOrigin);
            }
            //计数器，防止连点时规则混乱
            if (increaseReferece(request, this.config.getRequestRefName(), 1) != 1) {
                return true;
            }

            ContextUtil.enter(currentResourceName, parseOrigin);
            setAuthorityRules(currentResourceName);
            //因asyncEntry无法生成链路树，故还原为entry方法
            Entry entry = SphU.entry(currentResourceName, ResourceTypeConstants.COMMON_WEB, EntryType.IN);
            request.setAttribute(config.getRequestAttributeName(), entry);
            return true;
        } catch (BlockException e) {
            handleBlockException(request, response, e);
        } finally {
            ContextUtil.exit();
        }
        return false;
    }

    /**
     * 1. 自定义鉴权规则
     * 2. 拉取控制台鉴权规则
     * 3. 若自定义与控制台资源重复，以控制台为主 原因：loadRules为updateValue
     * {@link com.alibaba.csp.sentinel.slots.block.authority.AuthoritySlot#entry}
     * checkBlackWhiteAuthority方法会显示所有鉴权规则
     *
     * @param currentResourceName
     */
    private void setAuthorityRules(String currentResourceName) {
        List<String> limitAppList = configuration.getLimitApp();
        String limitAppStr = String.join(",", limitAppList);
        //如果已经存在该规则,直接返回,解决规则重复提示
        Map<String, String> rulesMap = AuthorityRuleManager.getRules().stream().collect(Collectors.toMap(AuthorityRule::getResource, AuthorityRule::getLimitApp));
        if (AuthorityRuleManager.hasConfig(currentResourceName) && Objects.equals(rulesMap.get(currentResourceName), limitAppStr)) {
            return;
        }
        AuthorityRule authorityRule = new AuthorityRule();
        authorityRule.setResource(currentResourceName);
        authorityRule.setLimitApp(limitAppStr);
        authorityRule.setStrategy(configuration.getStrategy());
        List<AuthorityRule> currentRules = AuthorityRuleManager.getRules();
        currentRules.add(authorityRule);
        AuthorityRuleManager.loadRules(currentRules);
    }

    private Integer increaseReferece(HttpServletRequest request, String rcKey, int step) {
        Object obj = request.getAttribute(rcKey);

        if (obj == null) {
            // initial
            obj = Integer.valueOf(0);
        }

        Integer newRc = (Integer) obj + step;
        request.setAttribute(rcKey, newRc);
        return newRc;
    }
}
