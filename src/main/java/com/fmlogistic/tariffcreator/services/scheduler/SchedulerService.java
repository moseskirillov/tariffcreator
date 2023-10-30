package com.fmlogistic.tariffcreator.services.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerService {

    private static final String ZOEK = "zoek";
    private static final String BJ_BD_USER = "itlctms";
    private static final String BJ_BD_PASSWORD = "itlctms123";
    private static final String BJ_BD_URL = "jdbc:oracle:thin:@172.31.0.13:1521:PHMKCHW";

    private static final String SEARCH = "серчей";
    private static final String CLIENT = "клиентов";

    private static final String SEARCH_NAMES_HASH = "searchNames";
    private static final String CLIENT_NAMES_HASH = "clientNames";

    private static final String IMPORT_COMPLETED_LOG = "Соединение закрыто. Импортированно {} {}";
    private static final String IMPORT_CLIENTS_STARTED_LOG = "Начался импорт клиентов из базы BlueJay";
    private static final String IMPORT_SEARCHES_STARTED_LOG = "Начался импорт серчнеймов из базы BlueJay";

    private static final String SELECT_SEARCH_NAMES_SQL = "SELECT distinct \"zoek\" FROM \"cef_relati\" WHERE \"srtrel\" = 1";
    private static final String SELECT_SEARCH_NAMES_WAREHOUSES_SQL = "SELECT DISTINCT \"zoek\" FROM ITLCTMS.ITRU_ADDR WHERE \"tsnam1\" LIKE '%Склад%'";

    private final JedisPool jedisPool;

    @Scheduled(initialDelay = 5000, fixedDelay = 3600000)
    public void updateSearchNames() {
        importClientsAndSearchNames(
            IMPORT_SEARCHES_STARTED_LOG,
            SELECT_SEARCH_NAMES_WAREHOUSES_SQL,
            SEARCH_NAMES_HASH,
            SEARCH
        );
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 7_200_000)
    public void updateClientNames() {
        importClientsAndSearchNames(
            IMPORT_CLIENTS_STARTED_LOG,
            SELECT_SEARCH_NAMES_SQL,
            CLIENT_NAMES_HASH, CLIENT);
    }

    private void importClientsAndSearchNames(String importSearchesStartedLog, String selectSearchNamesWarehousesSql, String searchNamesHash, String search) {
        log.info(importSearchesStartedLog);
        try (var connection = DriverManager.getConnection(BJ_BD_URL, BJ_BD_USER, BJ_BD_PASSWORD);
             var statement = connection.createStatement();
             var result = statement.executeQuery(selectSearchNamesWarehousesSql)
        ) {
            var count = 0;
            var searchNames = new HashMap<String, String>();
            while (result.next()) {
                searchNames.put(String.valueOf(count), result.getString(ZOEK));
                count++;
            }
            try (var jedis = jedisPool.getResource()) {
                jedis.select(5);
                jedis.del(searchNamesHash);
                jedis.hmset(searchNamesHash, searchNames);
            }
            log.info(IMPORT_COMPLETED_LOG, count, search);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}