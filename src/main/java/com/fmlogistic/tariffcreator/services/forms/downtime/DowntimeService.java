package com.fmlogistic.tariffcreator.services.forms.downtime;

import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeModel;

import java.util.List;

public interface DowntimeService {
    List<String> generate(DowntimeModel model);
}
