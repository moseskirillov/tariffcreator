package com.fmlogistic.tariffcreator.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Tariff Creator",
        description = "Сервис герерации тарифных файлов для BJ", version = "1.0.0",
        contact = @Contact(
            name = "Moses Kirillov",
            email = "mkirillov@fmlogsitic.com"
        ))
)
public class OpenApiConfig {
}
