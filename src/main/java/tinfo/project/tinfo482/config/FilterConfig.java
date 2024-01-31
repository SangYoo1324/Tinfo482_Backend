//package tinfo.project.tinfo482.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//
//@Configuration
//public class FilterConfig {
//
//@Bean
//    public FilterRegistrationBean<CustomCorsFilter> corsFilter(){
//    FilterRegistrationBean<CustomCorsFilter> registrationBean = new FilterRegistrationBean<>();
//    registrationBean.setFilter(new CustomCorsFilter());
//    registrationBean.addUrlPatterns("/*");
//    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//    return registrationBean;
//}
//}
//