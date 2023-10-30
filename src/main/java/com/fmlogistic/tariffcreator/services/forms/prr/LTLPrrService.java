package com.fmlogistic.tariffcreator.services.forms.prr;

import com.fmlogistic.tariffcreator.models.generator.prr.PrrModel;

import java.util.List;

public interface LTLPrrService {
    List<String> generate(PrrModel model);
}
