package com.fmlogistic.tariffcreator.controllers;

import com.fmlogistic.tariffcreator.adapter.GeneratorAdapter;
import com.fmlogistic.tariffcreator.models.generator.request.BaseRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Для генерации файлов XML", description = "Общий для всех типов тарифов")
public class GenerateController {

    private static final String NEW_GENERATE_REQUEST = "Получен запрос на генерацию тарифного файла";

    private final GeneratorAdapter generatorAdapter;

    @PostMapping("/api/generate")
    public ResponseEntity<Void> generate(@RequestBody BaseRequest request) {
        log.info(NEW_GENERATE_REQUEST);
        generatorAdapter.generate(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
