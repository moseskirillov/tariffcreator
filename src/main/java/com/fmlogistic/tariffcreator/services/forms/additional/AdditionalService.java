package com.fmlogistic.tariffcreator.services.forms.additional;

import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalModel;

import java.util.List;

public interface AdditionalService {
    List<String> generate(AdditionalModel model);
}
