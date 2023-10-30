package com.fmlogistic.tariffcreator.configuration;

import com.fmlogistic.tariffcreator.TariffCreatorApplication;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class FreemarkerConfig {

    private static final String UTF_ENCODING = "UTF-8";
    private static final String TEMPLATES_PACKAGE_NAME = "/ftl-templates";

    @Bean
    public freemarker.template.Configuration configuration() {
        var config = new freemarker.template.Configuration(new Version(2, 3, 20));
        config.setClassForTemplateLoading(TariffCreatorApplication.class, TEMPLATES_PACKAGE_NAME);
        config.setDefaultEncoding(UTF_ENCODING);
        config.setLocale(Locale.ENGLISH);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return config;
    }
}
