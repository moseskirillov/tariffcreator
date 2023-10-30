package com.fmlogistic.tariffcreator.services.forms;

import com.fmlogistic.tariffcreator.models.generator.request.PoolingRequest;

public interface PoolingService {
    void generate(PoolingRequest request);
}
