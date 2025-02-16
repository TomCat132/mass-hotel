package cn.finetool.common.configuration;

import cn.finetool.common.constant.GlobalNames;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public final class AppContext implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);
    private static ApplicationContext applicationContext = null;

    private static final ThreadLocal<Map<String, Object>> userCtx = ThreadLocal.withInitial(HashMap::new);

    public static <T> T getBean(Class<T> clz) throws BeansException {
        return applicationContext.getBean(clz);
    }

    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.applicationContext = applicationContext;
        LOGGER.info("ApplicationContext initialized with applicationContext: {}", applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static Object getThreadContext(String ctxKey, Object defaultVal) {
        Map ctxMap = (Map) userCtx.get();
        if (ctxMap == null) {
            return defaultVal;
        } else {
            if (ctxKey == null) return defaultVal;
            Object val = ctxMap.get(ctxKey);
            return val == null ? defaultVal : val;
        }
    }

    /**
     * 获取线程上下文参数
     *
     * @param ctxKey 上下文键
     * @return 上下文值
     */
    public static Object getThreadContext(String ctxKey) {
        return getThreadContext(ctxKey, null);
    }

    /**
     * 获取原始HttpServletRequest对象，不建议应用直接使用
     *
     * @return HttpServletRequest实例
     */
    public static HttpServletRequest getRawRequest() {
        return (HttpServletRequest) getThreadContext(GlobalNames.THREAD_CONTEXT_REQUEST_KEY);
    }

    /**
     * 获取原始HttpServletResponse对象，不建议应用直接使用
     *
     * @return HttpServletResponse实例
     */
    public static HttpServletResponse getRawResponse() {
        return (HttpServletResponse) getThreadContext(GlobalNames.THREAD_CONTEXT_RESPONSE_KEY);
    }

    public static void setRawRequest(HttpServletRequest request) {
        userCtx.get().put(GlobalNames.THREAD_CONTEXT_REQUEST_KEY, request);
    }

    public static void setRawResponse(HttpServletResponse response) {
        userCtx.get().put(GlobalNames.THREAD_CONTEXT_RESPONSE_KEY, response);
    }

    public static void clear() {
        userCtx.remove();
    }
}