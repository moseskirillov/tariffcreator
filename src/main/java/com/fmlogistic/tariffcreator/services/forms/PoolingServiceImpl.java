package com.fmlogistic.tariffcreator.services.forms;

import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalModel;
import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeModel;
import com.fmlogistic.tariffcreator.models.generator.overweight.OverweightModel;
import com.fmlogistic.tariffcreator.models.generator.request.PoolingRequest;
import com.fmlogistic.tariffcreator.models.generator.standard.GeneratePoolingRow;
import com.fmlogistic.tariffcreator.models.generator.standard.GeneratePoolingRows;
import com.fmlogistic.tariffcreator.models.generator.standard.GeneratePoolingStandard;
import com.fmlogistic.tariffcreator.models.generator.standard.PoolingRow;
import com.fmlogistic.tariffcreator.services.forms.additional.AdditionalService;
import com.fmlogistic.tariffcreator.services.forms.downtime.DowntimeService;
import com.fmlogistic.tariffcreator.services.forms.overweight.OverweightService;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitiesService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitySatelliteService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CommentFilesService;
import com.fmlogistic.tariffcreator.services.unisender.UnisenderService;
import com.fmlogistic.tariffcreator.utils.GenerateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ALCO_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.FOOD_TYPES;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MOSCOW_FIRST_ZONE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MOSCOW_FOURTH_ZONE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MOSCOW_SECOND_ZONE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MOSCOW_THIRD_ZONE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PLM;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PL_LT_RETAIL;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.PL_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.POOLING_FORM_NAME;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.REF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.R_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.T_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolingServiceImpl implements PoolingService {

    private static final String PF_DELIVERY = "%sPF_delivery_%s_%s";
    private static final String PF_DELIVERY_DESCRIPTION = "PF доставка TT";
    private static final String PF_MSC_DELIVERY = "%sPF_MSC_delivery_%s_%s";
    private static final String PF_MSC_DELIVERY_DESCRIPTION = "PF Мск TT";
    private static final String PF_ALCO_DELIVERY = "%sPF_alco_delivery_%s_%s";
    private static final String PF_ALCO_DELIVERY_DESCRIPTION = "PF доставка alco TT";
    private static final String PF_MSC_ALCO_DELIVERY = "%sPF_MSC_alco_delivery_%s_%s";
    private static final String PF_MSC_ALCO_DELIVERY_DESCRIPTION = "PF Мск alco TT";
    private static final String PL_DELIVERY = "%sPL_delivery_%s_%s";
    private static final String PL_DELIVERY_DESCRIPTION = "PL доставка TT";
    private static final String PL_MSC_DELIVERY = "%sPL_MSC_delivery_%s_%s";
    private static final String PL_MSC_DELIVERY_DESCRIPTION = "PL Мск TT";
    private static final String PL_ALCO_DELIVERY = "%sPL_alco_delivery_%s_%s";
    private static final String PL_ALCO_DELIVERY_DESCRIPTION = "PL доставка alco TT";
    private static final String PL_MSC_ALCO_DELIVERY = "%sPL_MSC_alco_delivery_%s_%s";
    private static final String PL_MSC_ALCO_DELIVERY_DESCRIPTION = "PL Мск alco TT";
    private static final String PF_MSC_PL_DELIVERY = "%sPoolingFix_MSC_PL_%s_%s";
    private static final String PF_MSC_PL_DELIVERY_DESCRIPTION = "PoolingFix PL Мск TT";
    private static final String PF_MSC_LT_DELIVERY = "%sPoolingFix_MSC_LT_%s_%s";
    private static final String PF_MSC_LT_DELIVERY_DESCRIPTION = "PoolingFix LT Мск TT";
    private static final String PF_PL_DELIVERY = "%sPoolingFix_PL_%s_%s";
    private static final String PF_PL_DELIVERY_DESCRIPTION = "PoolingFix PL доставка TT";
    private static final String PF_LT_DELIVERY = "%sPoolingFix_LT_%s_%s";
    private static final String PF_LT_DELIVERY_DESCRIPTION = "PoolingFix LT доставка TT";
    private static final String PF_PL_MSC_ALCO_DELIVERY = "%sPoolingFix_PL_MSC_alco_delivery_%s_%s";
    private static final String PF_PL_MSC_ALCO_DELIVERY_DESCRIPTION = "PoolingFix PL Мск alco TT";
    private static final String PF_LT_MSC_ALCO_DELIVERY = "%sPoolingFix_LT_MSC_alco_delivery_%s_%s";
    private static final String PF_LT_MSC_ALCO_DELIVERY_DESCRIPTION = "PoolingFix LT Мск alco TT";
    private static final String PF_PL_ALCO_DELIVERY = "%sPoolingFix_PL_alco_delivery_%s_%s";
    private static final String PF_PL_ALCO_DELIVERY_DESCRIPTION = "PoolingFix PL доставка alco TT";
    private static final String PF_LT_ALCO_DELIVERY = "%sPoolingFix_LT_alco_delivery_%s_%s";
    private static final String PF_LT_ALCO_DELIVERY_DESCRIPTION = "PoolingFix LT доставка alco TT";

    private static final String POOLING_FIX_PF_LT_TEMPLATE = "pooling-standard-pf-lt";
    private static final String POOLING_STANDARD_TEMPLATE = "pooling-standard";

    private static final String GENERATE_ERROR_MESSAGE = "Произошла ошибка генерации файла Pooling:";
    private static final String GENERATE_STARTED = "Началась генерация Pooling";
    private static final String GENERATE_POOLING_STARTED = "Началась генерация Pooling файла";
    private static final String GENERATE_DOWNTIME_STARTED = "Началась генерация простоя";
    private static final String GENERATE_OVERWEIGHT_STARTED = "Началась генерация перевеса";
    private static final String GENERATE_ADDITIONAL_STARTED = "Началась генерация доп. сервисов";

    private final CitiesService citiesService;
    private final DowntimeService downtimeService;
    private final GenerateService generateService;
    private final UnisenderService unisenderService;
    private final OverweightService overweightService;
    private final AdditionalService additionalService;
    private final CommentFilesService commentFilesService;
    private final CitySatelliteService citySatelliteService;

    @Async
    @Override
    public void generate(PoolingRequest request) {
        try {
            log.info(GENERATE_STARTED);
            log.info(GENERATE_POOLING_STARTED);
            var files = new ArrayList<>(generatePooling(request));
            log.info(GENERATE_DOWNTIME_STARTED);
            files.addAll(downtimeService.generate(mapRequestToDowntimeModel(request)));
            log.info(GENERATE_OVERWEIGHT_STARTED);
            files.addAll(overweightService.generate(mapRequestToOverweightModel(request)));
            log.info(GENERATE_ADDITIONAL_STARTED);
            files.addAll(additionalService.generate(mapRequestToAdditionalModel(request)));
            files.forEach(e -> commentFilesService.saveOrUpdate(e, request.getComment()));
            unisenderService.sendCreationMessage(
                request.getEmail(),
                POOLING_FORM_NAME,
                request.getClientName(),
                files,
                request.getDateFrom(),
                request.getDateTo(),
                request.getComment()
            );
        } catch (Exception e) {
            log.error(GENERATE_ERROR_MESSAGE, e);
            unisenderService.sendErrorMessage(request.getEmail(), request.getClientName(), request.getType());
            throw e;
        }
    }

    private List<String> generatePooling(PoolingRequest request) {
        var login = GenerateUtils.getLogin(request.getEmail());
        var files = new ArrayList<String>();
        var suppliers = StringUtils.isNoneBlank(request.getSuppliers()) ? PLM : StringUtils.EMPTY;
        var rowsList = mapStandardRows(request, login, suppliers);
        for (var i = 0; i < rowsList.size(); i++) {
            var rows = rowsList.get(i);
            if (CollectionUtils.isNotEmpty(rows.getRows())) {
                var templateName = rows.getFileName().equals(String.format(PF_MSC_LT_DELIVERY, suppliers, request.getClientName(), login))
                    || rows.getFileName().equals(String.format(PF_LT_DELIVERY, suppliers, request.getClientName(), login))
                    || rows.getFileName().equals(String.format(PF_LT_MSC_ALCO_DELIVERY, suppliers, request.getClientName(), login))
                    || rows.getFileName().equals(String.format(PF_LT_ALCO_DELIVERY, suppliers, request.getClientName(), login))
                    ? POOLING_FIX_PF_LT_TEMPLATE
                    : POOLING_STANDARD_TEMPLATE;
                var generateModel = new GeneratePoolingStandard(
                    request.getClientName(),
                    request.getDateFrom(),
                    GenerateUtils.plusDate(request.getDateTo(), 134 + i),
                    request.getSuppliers(),
                    GenerateUtils.finString(request.getCommodityCode(), false),
                    GenerateUtils.tariffFormula(request.getCommodityCode(), false),
                    rows.getDescription(),
                    rows.getRows()
                );
                files.add(generateService.generateTariffFile(templateName, generateModel, rows.getFileName()));
            }
        }
        return files;
    }

    private List<GeneratePoolingRows> mapStandardRows(PoolingRequest request, String login, String suppliers) {
        return List.of(
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PF_TYPE, false),
                String.format(PF_DELIVERY, suppliers, request.getClientName(), login),
                PF_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PF_TYPE, true),
                String.format(PF_MSC_DELIVERY, suppliers, request.getClientName(), login),
                PF_MSC_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PF_TYPE, false),
                String.format(PF_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PF_ALCO_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PF_TYPE, true),
                String.format(PF_MSC_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PF_MSC_ALCO_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PL_TYPE, false),
                String.format(PL_DELIVERY, suppliers, request.getClientName(), login),
                PL_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PL_TYPE, true),
                String.format(PL_MSC_DELIVERY, suppliers, request.getClientName(), login),
                PL_MSC_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PL_TYPE, false),
                String.format(PL_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PL_ALCO_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PL_TYPE, true),
                String.format(PL_MSC_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PL_MSC_ALCO_DELIVERY_DESCRIPTION,
                null
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PL_LT_RETAIL, true),
                String.format(PF_MSC_PL_DELIVERY, suppliers, request.getClientName(), login),
                PF_MSC_PL_DELIVERY_DESCRIPTION,
                PL_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PL_LT_RETAIL, true),
                String.format(PF_MSC_LT_DELIVERY, suppliers, request.getClientName(), login),
                PF_MSC_LT_DELIVERY_DESCRIPTION,
                LT_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PL_LT_RETAIL, false),
                String.format(PF_PL_DELIVERY, suppliers, request.getClientName(), login),
                PF_PL_DELIVERY_DESCRIPTION,
                PL_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, FOOD_TYPES, PL_LT_RETAIL, false),
                String.format(PF_LT_DELIVERY, suppliers, request.getClientName(), login),
                PF_LT_DELIVERY_DESCRIPTION,
                LT_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PL_LT_RETAIL, true),
                String.format(PF_PL_MSC_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PF_PL_MSC_ALCO_DELIVERY_DESCRIPTION,
                PL_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PL_LT_RETAIL, true),
                String.format(PF_LT_MSC_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PF_LT_MSC_ALCO_DELIVERY_DESCRIPTION,
                LT_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PL_LT_RETAIL, false),
                String.format(PF_PL_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PF_PL_ALCO_DELIVERY_DESCRIPTION,
                PL_TYPE
            ),
            mapToGenerateModel(
                filterByRegion(request, ALCO_TYPE, PL_LT_RETAIL, false),
                String.format(PF_LT_ALCO_DELIVERY, suppliers, request.getClientName(), login),
                PF_LT_ALCO_DELIVERY_DESCRIPTION,
                LT_TYPE
            )
        );
    }

    private GeneratePoolingRows mapToGenerateModel(List<PoolingRow> rows, String fileName, String description, String serviceType) {
        try {
            var resultRows = new ArrayList<GeneratePoolingRow>();
            var firstList = new ArrayList<GeneratePoolingRow>();
            var secondList = new ArrayList<GeneratePoolingRow>();
            var thirdList = new ArrayList<GeneratePoolingRow>();
            for (var row : rows) {
                var city = citiesService.findByName(row.getUnloadingCity());
                var satellites = citySatelliteService.citiesSatellites(row.getUnloadingCity());
                if (satellites.isEmpty()) {
                    if (city != null) {
                        if (city.isException()) {
                            firstList.add(generatePoolingRow(row, row.getUnloadingCity(), city.getRZone(), serviceType));
                        }
                        if (city.getRZone() == null && city.getCZone() != null) {
                            secondList.add(generatePoolingRow(row, StringUtils.SPACE, city.getCZone(), serviceType));
                        }
                        if (city.getCZone() != null && city.getRZone() != null) {
                            thirdList.add(generatePoolingRow(row, StringUtils.SPACE, city.getCZone(), serviceType));
                            thirdList.add(generatePoolingRow(row, StringUtils.SPACE, city.getRZone(), serviceType));
                        }
                    }
                } else {
                    for (var satellite : satellites) {
                        if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                            firstList.add(generatePoolingRow(row, satellite.getSatellite(), satellite.getRZone(), serviceType));
                        } else if (satellite.getCZone() != null) {
                            secondList.add(generatePoolingRow(row, satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), satellite.getCZone(), serviceType));
                        } else {
                            thirdList.add(generatePoolingRow(row, satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), satellite.getRZone(), serviceType));
                        }
                    }
                }
            }
            resultRows.addAll(firstList.stream().sorted(Comparator.comparing(GeneratePoolingRow::getBodyType)).toList());
            resultRows.addAll(secondList.stream().sorted(Comparator.comparing(GeneratePoolingRow::getBodyType)).toList());
            resultRows.addAll(thirdList.stream().sorted(Comparator.comparing(GeneratePoolingRow::getBodyType)).toList());
            return new GeneratePoolingRows(fileName, description, resultRows);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private GeneratePoolingRow generatePoolingRow(PoolingRow row, String unloadingCity, String zone, String serviceType) {
        return new GeneratePoolingRow(
            row.getCustomerWarehouse() == null || row.getCustomerWarehouse().equals(StringUtils.EMPTY)
                ? row.getWarehouseFm()
                : row.getCustomerWarehouse(),
            unloadingCity == null ? StringUtils.SPACE : unloadingCity,
            zone,
            row.getBodyType().equals(REF_TYPE) ? R_TYPE : T_TYPE,
            serviceType == null ? row.getDelivery() : serviceType,
            row.getLoadingUnit(),
            row.getOneFifteen(),
            validateRow(row.getSixteenTwenty(), row.getOneFifteen()),
            validateRow(row.getTwentyOneTwentyFive(), row.getOneFifteen()),
            validateRow(row.getTwentySix(), row.getOneFifteen()),
            validateRow(row.getTwentySeven(), row.getOneFifteen()),
            validateRow(row.getTwentyEight(), row.getOneFifteen()),
            validateRow(row.getTwentyNine(), row.getOneFifteen()),
            validateRow(row.getThirty(), row.getOneFifteen()),
            validateRow(row.getThirtyOne(), row.getOneFifteen()),
            validateRow(row.getThirtyTwo(), row.getOneFifteen()),
            validateRow(row.getThirtyThree(), row.getOneFifteen())
        );
    }

    private String validateRow(String row, String defaultValue) {
        return row == null || row.equals(StringUtils.EMPTY) ? defaultValue : row;
    }

    private List<PoolingRow> filterByRegion(PoolingRequest request, List<String> codes, String deliveryType, boolean moscow) {
        for (var code : codes) {
            if (code.equals(request.getCommodityCode())) {
                var rows = request.getStandard();
                return moscow
                    ?
                    rows.stream()
                        .filter(row -> deliveryType.equals(row.getDelivery()))
                        .filter(row ->
                            MOSCOW_FIRST_ZONE.equals(row.getUnloadingCity())
                                || MOSCOW_SECOND_ZONE.equals(row.getUnloadingCity())
                                || MOSCOW_THIRD_ZONE.equals(row.getUnloadingCity())
                                || MOSCOW_FOURTH_ZONE.equals(row.getUnloadingCity()))
                        .toList()
                    :
                    rows.stream()
                        .filter(row -> deliveryType.equals(row.getDelivery()))
                        .filter(row ->
                            !MOSCOW_FIRST_ZONE.equals(row.getUnloadingCity())
                                && !MOSCOW_SECOND_ZONE.equals(row.getUnloadingCity())
                                && !MOSCOW_THIRD_ZONE.equals(row.getUnloadingCity())
                                && !MOSCOW_FOURTH_ZONE.equals(row.getUnloadingCity())
                        ).toList();
            }
        }
        return List.of();
    }

    private DowntimeModel mapRequestToDowntimeModel(PoolingRequest request) {
        return new DowntimeModel(
            request.getType(),
            request.getClientName(),
            request.getEmail(),
            request.getDateFrom(),
            request.getDateTo(),
            request.getSuppliers(),
            request.getDowntimes()
        );
    }

    private OverweightModel mapRequestToOverweightModel(PoolingRequest request) {
        return new OverweightModel(
            request.getType(),
            request.getClientName(),
            request.getEmail(),
            request.getDateFrom(),
            request.getDateTo(),
            request.getSuppliers(),
            request.getOverweight()
        );
    }

    private AdditionalModel mapRequestToAdditionalModel(PoolingRequest request) {
        return new AdditionalModel(
            request.getClientName(),
            request.getEmail(),
            request.getDateFrom(),
            request.getDateTo(),
            request.getAdditional()
        );
    }
}
