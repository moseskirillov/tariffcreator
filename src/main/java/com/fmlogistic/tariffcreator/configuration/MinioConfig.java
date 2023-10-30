package com.fmlogistic.tariffcreator.configuration;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private static final String MINIO_ENDPOINT = "http://217.73.62.119:9000";
    private static final String MINIO_ACCESS = "UvRILLBWjAam1fOGhM5b";
    private static final String MINIO_SECRET = "aodFrsecSZIMrl5K3dh1";

    @Bean
    public MinioClient minioClient() {
        return MinioClient
            .builder()
            .endpoint(MINIO_ENDPOINT)
            .credentials(MINIO_ACCESS, MINIO_SECRET)
            .build();
    }
}
