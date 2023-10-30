package com.fmlogistic.tariffcreator.controllers;

import com.fmlogistic.tariffcreator.entities.City;
import com.fmlogistic.tariffcreator.repositories.CityRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Получение городов, серчнеймов, клиентов", description = "Нужно для заполнения выпадающих список и привязки генерируемых файлов к клиенту")
public class ResourcesController {

    private static final String SEARCH_NAMES_HASH = "searchNames";
    private static final String CLIENT_NAMES_HASH = "clientNames";

    private final CityRepository cityRepository;
    private final JedisPool jedisPool;

    @GetMapping("/search/all")
    public ResponseEntity<List<String>> getSearchNames() {
        return ResponseEntity.ok(getAllValuesFromRedisByHashName(SEARCH_NAMES_HASH));
    }

    @GetMapping("/cities/all")
    public ResponseEntity<List<City>> getCities() {
        return ResponseEntity.ok(cityRepository.findAll());
    }

    @GetMapping("/clients/all")
    public ResponseEntity<List<String>> getClients() {
        return ResponseEntity.ok(getAllValuesFromRedisByHashName(CLIENT_NAMES_HASH));
    }

    private List<String> getAllValuesFromRedisByHashName(String hashName) {
        try (var jedis = jedisPool.getResource()) {
            jedis.select(5);
            return jedis.hgetAll(hashName)
                .values()
                .stream()
                .sorted()
                .toList();
        } catch (Exception e) {
            log.error("Ошибка при получении клиентов: ", e);
            return List.of();
        }
    }

}
