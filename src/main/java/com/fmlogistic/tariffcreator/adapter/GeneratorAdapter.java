package com.fmlogistic.tariffcreator.adapter;

import com.fmlogistic.tariffcreator.models.generator.request.BaseRequest;

public interface GeneratorAdapter {
    <T extends BaseRequest> void generate(T model);
}
