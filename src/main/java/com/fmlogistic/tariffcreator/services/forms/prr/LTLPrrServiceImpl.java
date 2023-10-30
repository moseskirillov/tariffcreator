package com.fmlogistic.tariffcreator.services.forms.prr;

import com.fmlogistic.tariffcreator.models.generator.prr.GeneratePrr;
import com.fmlogistic.tariffcreator.models.generator.prr.GeneratePrrRow;
import com.fmlogistic.tariffcreator.models.generator.prr.PrrModel;
import com.fmlogistic.tariffcreator.models.generator.prr.PrrRow;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_202;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_207;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MG_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PLM;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.getLogin;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.plusDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LTLPrrServiceImpl implements LTLPrrService {

    private static final String PRR = "ПРР";
    private static final String ROLLING_OUT = "выкатка";
    private static final String GENERATE_TEMPLATE = "prr";
    private static final String LT_PRR_HANDLING = "%sLT_PRR_handling_%s_%s";
    private static final String MG_PRR_HANDLING = "%sMG_PRR_handling_%s_%s";
    private static final String LT_PRR_MECHANIZED = "%sLT_PRR_mechanized_%s_%s";
    private static final String MG_PRR_MECHANIZED = "%sMG_PRR_mechanized_%s_%s";
    private static final String FILE_DESCRIPTION = "%s %s TC";
    private static final String PRR_HANDLING = "Погрузо-разгрузочные работы водителем, (ручные)";
    private static final String PRR_MECHANIZED = "Погрузо-разгрузочные работы водителем, (механизированные)";

    private final GenerateService generateService;

    @Override
    public List<String> generate(PrrModel model) {
        var files = new ArrayList<String>();
        var login = getLogin(model.getEmail());
        var ltHandling = createGenerateModel(model, LT_TYPE, PRR_HANDLING, 161);
        var ltMechanized = createGenerateModel(model, LT_TYPE, PRR_MECHANIZED, 162);
        var mgHandling = createGenerateModel(model, MG_TYPE, PRR_HANDLING, 163);
        var mgMechanized = createGenerateModel(model, MG_TYPE, PRR_MECHANIZED, 164);
        generateFilesModels(model, files, login, ltHandling, ltMechanized, LT_PRR_HANDLING, LT_PRR_MECHANIZED);
        generateFilesModels(model, files, login, mgHandling, mgMechanized, MG_PRR_HANDLING, MG_PRR_MECHANIZED);
        return files;
    }

    private void generateFilesModels(PrrModel model, ArrayList<String> files, String login, GeneratePrr mgHandling, GeneratePrr mgMechanized, String mgPrrHandling, String mgPrrMechanized) {
        if (mgHandling != null) {
            files.add(generate(mgHandling, String.format(mgPrrHandling, StringUtils.isNoneBlank(model.getSuppliers()) ? PLM : StringUtils.EMPTY, model.getClientName(), login)));
        }
        if (mgMechanized != null) {
            files.add(generate(mgMechanized, String.format(mgPrrMechanized, StringUtils.isNoneBlank(model.getSuppliers()) ? PLM : StringUtils.EMPTY, model.getClientName(), login)));
        }
    }

    private String generate(GeneratePrr generatePrr, String fileName) {
        return generateService.generateTariffFile(GENERATE_TEMPLATE, generatePrr, fileName);
    }

    private GeneratePrr createGenerateModel(PrrModel model, String serviceType, String serviceName, int plusDate) {
        var row = filterRows(model.getRows(), serviceType, serviceName).orElse(null);
        if (row == null) {
            return null;
        }
        return new GeneratePrr(
            model.getClientName(),
            model.getDateFrom(),
            plusDate(model.getDateTo(), plusDate),
            model.getSuppliers(),
            serviceName.equals(PRR_HANDLING) ? COMMODITY_202 : COMMODITY_207,
            String.format(FILE_DESCRIPTION, serviceType, serviceName.equals(PRR_HANDLING) ? PRR : ROLLING_OUT),
            new GeneratePrrRow(
                serviceType,
                row.getServiceType(),
                String.valueOf(Double.parseDouble(row.getCost()) * 0.001),
                row.getCost()
            )
        );
    }

    private Optional<PrrRow> filterRows(List<PrrRow> rows, String serviceType, String serviceName) {
        return rows
            .stream()
            .filter(e -> StringUtils.isNoneBlank(e.getCost()))
            .filter(e -> serviceType.equals(e.getServiceType()))
            .filter(e -> serviceName.equals(e.getServiceName()))
            .findFirst();
    }
}
