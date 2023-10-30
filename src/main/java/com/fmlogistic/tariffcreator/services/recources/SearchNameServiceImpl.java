package com.fmlogistic.tariffcreator.services.recources;

import com.fmlogistic.tariffcreator.entities.SearchName;
import com.fmlogistic.tariffcreator.repositories.SearchNameRepository;
import com.fmlogistic.tariffcreator.services.recources.interfaces.SearchNameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchNameServiceImpl implements SearchNameService {

    private final SearchNameRepository searchNameRepository;

    @Override
    @Transactional
    public void deleteAllSearchNames() {
        searchNameRepository.deleteAll();
        searchNameRepository.restartIndexes();
    }

    @Override
    @Transactional
    public void saveSearchName(String name) {
        var searchName = new SearchName();
        searchName.setName(name);
        searchNameRepository.save(searchName);
    }
}
