package com.fmlogistic.tariffcreator.services.forms.overweight;

import com.fmlogistic.tariffcreator.models.generator.overweight.GenerateOverweight;
import com.fmlogistic.tariffcreator.models.generator.overweight.OverweightModel;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
import com.fmlogistic.tariffcreator.utils.GenerateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LD_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LTL_TARIFF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LZ_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MG_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PLM;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PL_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.POOLING_TARIFF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZL_TYPE;

@Service
@RequiredArgsConstructor
public class OverweightServiceImpl implements OverweightService {

    private static final String OVERWEIGHT = "overweight";
    private static final String OVERWEIGHT_FILE_NAME = "%s%s_overweight_%s_%s";

    private final GenerateService generateService;

    @Override
    public List<String> generate(OverweightModel model) {
        var login = GenerateUtils.getLogin(model.getEmail());
        var files = new ArrayList<String>();
        if (model.getType().equals(LTL_TARIFF_TYPE)) {
            files.addAll(generateLTLOverweight(model, login));
        } else if (model.getType().equals(POOLING_TARIFF_TYPE)) {
            files.addAll(generatePoolingOverweight(model, login));
        }
        return files;
    }

    private List<String> generateLTLOverweight(OverweightModel overweightModel, String login) {
        var files = new ArrayList<String>();
        var models = List.of(
            mapOverweightModelToGenerateData(overweightModel, LT_TYPE, 132),
            mapOverweightModelToGenerateData(overweightModel, MG_TYPE, 133),
            mapOverweightModelToGenerateData(overweightModel, LZ_TYPE, 165),
            mapOverweightModelToGenerateData(overweightModel, ZL_TYPE, 166)
        );
        return createFiles(overweightModel, login, files, models);
    }

    private List<String> generatePoolingOverweight(OverweightModel overweightModel, String login) {
        var files = new ArrayList<String>();
        var models = List.of(
            mapOverweightModelToGenerateData(overweightModel, PL_TYPE, 156),
            mapOverweightModelToGenerateData(overweightModel, PF_TYPE, 157),
            mapOverweightModelToGenerateData(overweightModel, LD_TYPE, 158)
        );
        return createFiles(overweightModel, login, files, models);
    }

    private List<String> createFiles(OverweightModel overweightModel, String login, ArrayList<String> files, List<GenerateOverweight> models) {
        for (var model : models) {
            if (model.getRow() != null) {
                files.add(generateService.generateTariffFile(
                    OVERWEIGHT,
                    model,
                    String.format(OVERWEIGHT_FILE_NAME, StringUtils.isNoneBlank(overweightModel.getSuppliers()) ? PLM : StringUtils.EMPTY, model.getServiceType(), overweightModel.getClientName(), login))
                );
            }
        }
        return files;
    }

    private GenerateOverweight mapOverweightModelToGenerateData(OverweightModel model, String serviceType, int plusDate) {
        return new GenerateOverweight(
            model.getClientName(),
            model.getDateFrom(),
            GenerateUtils.plusDate(model.getDateTo(), plusDate),
            serviceType,
            model.getSuppliers(),
            model.getOverweight()
                .stream()
                .filter(e -> serviceType.equals(e.getServiceType()))
                .filter(e -> StringUtils.isNoneBlank(e.getNorm()))
                .findFirst().orElse(null)
        );
    }
}
