package com.fmlogistic.tariffcreator.services.forms;

import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;

public interface FTLService {
    void generate(FTLRequest request);
}
