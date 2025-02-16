package cn.finetool.common.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ContextFilter> contextFilter() {
        FilterRegistrationBean<ContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContextFilter());
        registrationBean.addUrlPatterns("/*"); // 应用到所有 URL
        registrationBean.setOrder(2); // 设置过滤器的执行顺序
        return registrationBean;
    }
}