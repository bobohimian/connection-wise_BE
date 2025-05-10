//package com.conwise.config;
//
//import com.conwise.interceptor.AuthenticationInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Autowired
//    private AuthenticationInterceptor authenticationInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 添加拦截器并配置拦截路径
//        registry.addInterceptor(authenticationInterceptor)
//                .addPathPatterns("/**")  // 拦截所有请求
//                .excludePathPatterns(    // 排除不需要拦截的路径
//                        "/api/user/login",    // 登录接口
//                        "/api/user/register", // 注册接口
//                        "/error",        // 错误页面
//                        "/static/**"     // 静态资源
//                );
//    }
//}