package cn.finetool.common.configuration;


import cn.finetool.common.configuration.AppContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            AppContext.setRawRequest((HttpServletRequest) request);
            AppContext.setRawResponse((HttpServletResponse) response);
            chain.doFilter(request, response);
        } finally {
            AppContext.clear();
        }
    }

    @Override
    public void destroy() {
        AppContext.clear();
    }
}

