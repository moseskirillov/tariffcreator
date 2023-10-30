package com.fmlogistic.tariffcreator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String MAPPING_TEMPLATE = "/**";
    private static final String URL_PATH_PATTERN = "/{spring:\\w+}";
    private static final String FORWARD_VIEW_NAME = "forward:/";

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping(MAPPING_TEMPLATE);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(URL_PATH_PATTERN).setViewName(FORWARD_VIEW_NAME);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
