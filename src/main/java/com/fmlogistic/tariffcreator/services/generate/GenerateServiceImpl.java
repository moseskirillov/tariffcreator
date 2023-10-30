package com.fmlogistic.tariffcreator.services.generate;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import com.fmlogistic.tariffcreator.services.minio.MinioService;
import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenerateServiceImpl implements GenerateService {

    private static final String SUFFIX = ".xml";
    private static final String TEMPLATE = "%s.ftl";
    private static final String FILE_NAME_TEMPLATE = "%s%s";
    private static final String ROWS_TEMPLATE = "rows";
    private static final String BUCKET_CREATED_NAME = "tariffcreated";
    private static final String BUCKET_STORAGE_NAME = "tarifffiles";

    private final Configuration freemarker;
    private final MinioService minioService;
    private final ResourceLoader resourceLoader;

    @Override
    public <T extends BaseModel> String generateTariffFile(String templateName, T model, String fileName) {
        try {
            var resourcePath = Paths.get(resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX).getURI());
            var filePath = resourcePath.resolve(String.format(FILE_NAME_TEMPLATE, fileName, SUFFIX));
            var file = filePath.toFile();
            try (var writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                freemarker.getTemplate(String.format(TEMPLATE, templateName))
                        .process(Map.of(ROWS_TEMPLATE, model), writer);
            }
            minioService.uploadFile(BUCKET_CREATED_NAME, file);
            minioService.uploadFile(BUCKET_STORAGE_NAME, file);
            var xmlFileName = file.getName();
            Files.delete(file.toPath());
            return xmlFileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
