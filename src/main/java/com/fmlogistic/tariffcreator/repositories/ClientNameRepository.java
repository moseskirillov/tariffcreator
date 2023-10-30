package com.fmlogistic.tariffcreator.repositories;

import com.fmlogistic.tariffcreator.entities.ClientName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientNameRepository extends JpaRepository<ClientName, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "ALTER SEQUENCE IF EXISTS clients_seq RESTART WITH 1")
    void restartIndexes();

    List<ClientName> findByOrderByNameAsc();
}
