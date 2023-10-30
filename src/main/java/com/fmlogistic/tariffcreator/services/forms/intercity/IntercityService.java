package com.fmlogistic.tariffcreator.services.forms.intercity;

import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;

import java.util.List;

public interface IntercityService {
    List<String> generate(FTLRequest request);
}
