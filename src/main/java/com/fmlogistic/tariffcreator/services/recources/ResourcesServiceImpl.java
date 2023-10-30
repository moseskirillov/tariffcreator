package com.fmlogistic.tariffcreator.services.recources;

import com.fmlogistic.tariffcreator.services.recources.interfaces.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourcesServiceImpl implements ResourcesService {

    private static final String SEARCH_NAMES_HASH = "searchNames";
    private static final String CLIENT_NAMES_HASH = "clientNames";

    private final JedisPool jedisPool;

    @Override
    public List<String> getSearchNames() {
        try (var jedis = jedisPool.getResource()) {
            jedis.select(5);
            var result = jedis.hgetAll(SEARCH_NAMES_HASH);
            return result.values().stream().sorted().toList();
        }
    }

    @Override
    public List<String> getClientsNames() {
        try (var jedis = jedisPool.getResource()) {
            jedis.select(5);
            var result = jedis.hgetAll(CLIENT_NAMES_HASH);
            return result.values().stream().sorted().toList();
        }
    }
}
