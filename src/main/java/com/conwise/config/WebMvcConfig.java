package com.conwise.config;

import com.conwise.interceptor.AuthenticationInterceptor;
import com.conwise.interceptor.MdcInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final MdcInterceptor mdcInterceptor;

    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor, MdcInterceptor mdcInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.mdcInterceptor = mdcInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并指定拦截的路径
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/user/login", "/user/register", "/user/check-auth"); // 排除不需要拦截的路径
        registry.addInterceptor(mdcInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/user/login", "/user/register", "/user/check-auth"); // 排除不需要拦截的路径
    }
}