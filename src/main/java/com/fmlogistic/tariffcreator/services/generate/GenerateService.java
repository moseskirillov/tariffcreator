package com.fmlogistic.tariffcreator.services.generate;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;

public interface GenerateService {
    <T extends BaseModel> String generateTariffFile(String templateName, T model, String fileName);
}
