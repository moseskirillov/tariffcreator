package com.fmlogistic.tariffcreator.services.forms.overweight;

import com.fmlogistic.tariffcreator.models.generator.overweight.OverweightModel;

import java.util.List;

public interface OverweightService {
    List<String> generate(OverweightModel overweight);
}
