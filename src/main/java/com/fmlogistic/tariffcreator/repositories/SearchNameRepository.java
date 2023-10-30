package com.fmlogistic.tariffcreator.repositories;

import com.fmlogistic.tariffcreator.entities.SearchName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchNameRepository extends JpaRepository<SearchName, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "ALTER SEQUENCE IF EXISTS search_seq RESTART WITH 1")
    void restartIndexes();
    List<SearchName> findByOrderByNameAsc();
}