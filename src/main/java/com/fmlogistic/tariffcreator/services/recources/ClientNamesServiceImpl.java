package com.fmlogistic.tariffcreator.services.recources;

import com.fmlogistic.tariffcreator.entities.ClientName;
import com.fmlogistic.tariffcreator.repositories.ClientNameRepository;
import com.fmlogistic.tariffcreator.services.recources.interfaces.ClientNamesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientNamesServiceImpl implements ClientNamesService {

    private final ClientNameRepository clientNameRepository;

    @Override
    @Transactional
    public void saveClientName(String name) {
        var clientName = new ClientName();
        clientName.setName(name);
        clientNameRepository.save(clientName);
    }

    @Override
    @Transactional
    public void deleteAll() {
        clientNameRepository.deleteAll();
        clientNameRepository.restartIndexes();
    }
}
