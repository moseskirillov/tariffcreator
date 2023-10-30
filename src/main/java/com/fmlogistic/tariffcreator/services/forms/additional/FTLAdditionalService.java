package com.fmlogistic.tariffcreator.services.forms.additional;

import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;

import java.util.List;

public interface FTLAdditionalService {
    List<String> generateAdditional(FTLRequest request);
}
