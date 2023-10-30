package com.fmlogistic.tariffcreator.services.forms;

import com.fmlogistic.tariffcreator.models.generator.request.LTLRequest;

public interface LTLService {
    void generate(LTLRequest request);
}
