package com.fmlogistic.tariffcreator.services.forms.downtime;

import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeModel;
import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeRow;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateDowntime;
import com.fmlogistic.tariffcreator.models.generator.downtime.RateModel;
import com.fmlogistic.tariffcreator.models.generator.downtime.RatesModel;
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
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MG_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PLM;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PL_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.POOLING_TARIFF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZERO_STRING;

@Service
@RequiredArgsConstructor
public class DowntimeServiceImpl implements DowntimeService {

    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";

    private static final String MOSCOW = "Москва и МО";
    private static final String REGIONS = "Регионы";

    private static final String DOWNTIME_MOSCOW_TEMPLATE_NAME = "downtime_MOSCOW";
    private static final String DOWNTIME_REGION_TEMPLATE_NAME = "downtime_REGION";

    private static final String DOWNTIME_MOSCOW_FILE_NAME = "%s%s_downtime_MOSCOW_%s_%s";
    private static final String DOWNTIME_REGION_FILE_NAME = "%s%s_downtime_REGION_%s_%s";

    private final GenerateService generateService;

    @Override
    public List<String> generate(DowntimeModel model) {
        var files = new ArrayList<String>();
        var login = GenerateUtils.getLogin(model.getEmail());
        if (model.getType().equals(LTL_TARIFF_TYPE)) {
            files.addAll(generateLTLDowntime(model, login));
        } else if (model.getType().equals(POOLING_TARIFF_TYPE)) {
            files.addAll(generatePoolingDowntime(model, login));
        }
        return files;
    }

    private String getFileName(String fileNameTemplate, String suppliers, String serviceType, String clientName, String login) {
        return String.format(
            fileNameTemplate,
            StringUtils.isNoneBlank(suppliers) ? PLM : StringUtils.EMPTY,
            serviceType,
            clientName,
            login
        );
    }

    private List<String> generateLTLDowntime(DowntimeModel model, String login) {
        var files = new ArrayList<String>();
        var ltDowntimeMoscowModel = mapDowntimeModelToGenerateData(model, MOSCOW, LT_TYPE, 128);
        var ltDowntimeRegionModel = mapDowntimeModelToGenerateData(model, REGIONS, LT_TYPE, 129);
        var mgDowntimeMoscowModel = mapDowntimeModelToGenerateData(model, MOSCOW, MG_TYPE, 130);
        var mgDowntimeRegionModel = mapDowntimeModelToGenerateData(model, REGIONS, MG_TYPE, 131);
        generateFileName(model, login, files, ltDowntimeMoscowModel, ltDowntimeRegionModel);
        generateFileName(model, login, files, mgDowntimeMoscowModel, mgDowntimeRegionModel);
        return files;
    }

    private void generateFileName(DowntimeModel model, String login, ArrayList<String> files, GenerateDowntime mgDowntimeMoscowModel, GenerateDowntime mgDowntimeRegionModel) {
        if (mgDowntimeMoscowModel.getMoscowTwelveRow() != null && mgDowntimeMoscowModel.getMoscowTwentyFourRow() != null) {
            generate(
                files,
                mgDowntimeMoscowModel,
                getFileName(
                    DOWNTIME_MOSCOW_FILE_NAME,
                    model.getSuppliers(),
                    mgDowntimeMoscowModel.getServiceType(),
                    model.getClientName(),
                    login
                ),
                DOWNTIME_MOSCOW_TEMPLATE_NAME
            );
        }
        if (mgDowntimeRegionModel.getRegionsTwentyFourRow() != null) {
            generate(
                files,
                mgDowntimeRegionModel,
                getFileName(
                    DOWNTIME_REGION_FILE_NAME,
                    model.getSuppliers(),
                    mgDowntimeRegionModel.getServiceType(),
                    model.getClientName(),
                    login
                ),
                DOWNTIME_REGION_TEMPLATE_NAME
            );
        }
    }

    private List<String> generatePoolingDowntime(DowntimeModel model, String login) {
        var files = new ArrayList<String>();
        var plDowntimeMoscowModel = mapDowntimeModelToGenerateData(model, MOSCOW, PL_TYPE, 150);
        var plDowntimeRegionModel = mapDowntimeModelToGenerateData(model, REGIONS, PL_TYPE, 151);
        var pfDowntimeMoscowModel = mapDowntimeModelToGenerateData(model, MOSCOW, PF_TYPE, 152);
        var pfDowntimeRegionModel = mapDowntimeModelToGenerateData(model, REGIONS, PF_TYPE, 153);
        var ldDowntimeMoscowModel = mapDowntimeModelToGenerateData(model, MOSCOW, LD_TYPE, 154);
        var ldDowntimeRegionModel = mapDowntimeModelToGenerateData(model, REGIONS, LD_TYPE, 155);
        generateFileName(model, login, files, plDowntimeMoscowModel, plDowntimeRegionModel);
        generateFileName(model, login, files, pfDowntimeMoscowModel, pfDowntimeRegionModel);
        generateFileName(model, login, files, ldDowntimeMoscowModel, ldDowntimeRegionModel);
        return files;
    }

    private void generate(List<String> files, GenerateDowntime model, String fileName, String templateName) {
        files.add(generateService.generateTariffFile(templateName, model, fileName));
    }

    private GenerateDowntime mapDowntimeModelToGenerateData(DowntimeModel model, String region, String serviceType, int plusDate) {
        return new GenerateDowntime(
            model.getClientName(),
            model.getDateFrom(),
            GenerateUtils.plusDate(model.getDateTo(), plusDate),
            model.getSuppliers(),
            serviceType,
            createRateModel(model.getRows(), region, serviceType, HOURS_12),
            createRateModel(model.getRows(), region, serviceType, HOURS_24),
            createRateModel(model.getRows(), region, serviceType, HOURS_24)
        );
    }

    private RatesModel createRateModel(List<DowntimeRow> rows, String region, String serviceType, String time) {
        var ratesModel = new RatesModel();
        rows.forEach(row -> {
            if (region.equals(row.getRegion())
                && (serviceType.equals(row.getServiceType()))
                && (time.equals(row.getTime()))) {
                ratesModel.setTent(createRateModel(row.getTent()));
                ratesModel.setIzoterm(createRateModel(row.getIzoterm()));
                ratesModel.setRef(createRateModel(row.getRef()));
            }
        });
        if (ratesModel.getTent() == null && ratesModel.getIzoterm() == null && ratesModel.getRef() == null) {
            return null;
        }
        return ratesModel;
    }

    private RateModel createRateModel(String rate) {
        return rate == null || ZERO_STRING.equals(rate) || StringUtils.EMPTY.equals(rate)
            ? null
            : new RateModel(Integer.parseInt(rate) * 0.001, Double.parseDouble("999999"), Double.parseDouble(rate));
    }
}
