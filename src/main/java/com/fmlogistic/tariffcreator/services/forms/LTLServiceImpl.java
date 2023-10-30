package com.fmlogistic.tariffcreator.services.forms;

import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeModel;
import com.fmlogistic.tariffcreator.models.generator.overweight.OverweightModel;
import com.fmlogistic.tariffcreator.models.generator.prr.PrrModel;
import com.fmlogistic.tariffcreator.models.generator.request.LTLRequest;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateLTLRow;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateLTLRows;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateLTLStandard;
import com.fmlogistic.tariffcreator.models.generator.standard.LTLStandard;
import com.fmlogistic.tariffcreator.models.resources.CityModel;
import com.fmlogistic.tariffcreator.services.forms.downtime.DowntimeService;
import com.fmlogistic.tariffcreator.services.forms.overweight.OverweightService;
import com.fmlogistic.tariffcreator.services.forms.prr.LTLPrrService;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LTLServiceImpl implements LTLService {

    private static final String LTL_LP_TEMPLATE = "ltl-standard-lp";
    private static final String LTL_STANDARD_TEMPLATE = "ltl-standard";
    private static final String GENERATE_STARTED = "Началась генерация LTL";
    private static final String GENERATE_WITH_RETURN_STARTED = "Начата генерация LTL с возвратами";
    private static final String GENERATE_WITHOUT_RETURN_STARTED = "Начата генерация LTL без возвратов";
    private static final String UNLOADING_CITY_SET = "Получен город выгрузки: {}";
    private static final String CITY_FROM_DB_SET = "Получен город из БД: {}";
    private static final String MOSCOW_ZONE_SET = "Определена Московская зона";
    private static final String SATELLITES_CITIES_SET = "Получен список городов спутников из БД: {}";
    private static final String CITY_DETECT_AS_EXCEPTION = "Город {} определен как исключение и добавлен в первую очередь";
    private static final String CITY_ADD_TO_SECOND_QUEUE = "Город {} добавлен во вторую очередь";
    private static final String CITY_ADD_TO_THIRD_QUEUE = "Город {} добавлен в третью очередь";
    private static final String PROCESSING_WITH_CITY_IS_COMPLETE = "Работа с городом {} закочена";
    private static final String WORK_WITH_CITIES_SATELLITES = "Начата работа с гордами спутниками";
    private static final String CITY_SATELLITE_DETECT_AS_EXCEPTION = "Город-спутник {} добавлен в первую очередь";
    private static final String CITY_SATELLITE_ADD_TO_SECOND_QUEUE = "Город-спутник {} добавлен во вторую очередь";
    private static final String CITY_SATELLITE_ADD_TO_THIRD_QUEUE = "Город-спутник {} добавлен в третью очередь";

    private static final String GENERATE_ERROR_MESSAGE = "Произошла ошибка генерации файла LTL:";

    private final CitiesService citiesService;
    private final LTLPrrService ltlPrrService;
    private final DowntimeService downtimeService;
    private final GenerateService generateService;
    private final UnisenderService unisenderService;
    private final OverweightService overweightService;
    private final CommentFilesService commentFilesService;
    private final CitySatelliteService citySatelliteService;

    @Async
    @Override
    public void generate(LTLRequest request) {
        try {
            log.info(GENERATE_STARTED);
            var files = new ArrayList<String>();
            files.addAll(generateLtl(request));
            files.addAll(generateRailway(request));
            files.addAll(downtimeService.generate(mapRequestToDowntimeModel(request)));
            files.addAll(overweightService.generate(mapRequestToOverweightModel(request)));
            files.addAll(ltlPrrService.generate(mapRequestToPrrModel(request)));
            files.forEach(e -> commentFilesService.saveOrUpdate(e, request.getComment()));
            unisenderService.sendCreationMessage(
                request.getEmail(),
                LTL_TYPE,
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

    private Collection<String> generateRailway(LTLRequest request) {
        var files = new ArrayList<String>();
        var login = GenerateUtils.getLogin(request.getEmail());
        var rowsList = List.of(
            generateLTLRows(
                String.format("%sLZ_EP_other_RGN_%s_%s", StringUtils.isNoneBlank(request.getSuppliers()) ? PLM : StringUtils.EMPTY, request.getClientName(), login),
                "LZ EP другое TC",
                filterRailwayRows(request.getRailway(), LZ_TYPE, CONSIGNEE_TYPE_OTHER)
            ),
            generateLTLRows(
                String.format("%sLZ_EP_retail_RGN_%s_%s", StringUtils.isNoneBlank(request.getSuppliers()) ? PLM : StringUtils.EMPTY, request.getClientName(), login),
                "LZ EP сети TC",
                filterRailwayRows(request.getRailway(), LZ_TYPE, CONSIGNEE_TYPE_RETAIL)
            ),
            generateLTLRows(
                String.format("%sZL_EP_other_RGN_%s_%s", StringUtils.isNoneBlank(request.getSuppliers()) ? PLM : StringUtils.EMPTY, request.getClientName(), login),
                "ZL EP другое TC",
                filterRailwayRows(request.getRailway(), ZL_TYPE, CONSIGNEE_TYPE_OTHER)
            ),
            generateLTLRows(
                String.format("%sZL_EP_retail_RGN_%s_%s", StringUtils.isNoneBlank(request.getSuppliers()) ? PLM : StringUtils.EMPTY, request.getClientName(), login),
                "ZL EP сети TC",
                filterRailwayRows(request.getRailway(), ZL_TYPE, CONSIGNEE_TYPE_RETAIL)
            )
        );
        for (int i = 0; i < rowsList.size(); i++) {
            var rows = rowsList.get(i);
            if (CollectionUtils.isNotEmpty(rows.getRows())) {
                files.add(generateService.generateTariffFile(
                    LTL_STANDARD_TEMPLATE,
                    new GenerateLTLStandard(
                        request.getClientName(),
                        rows.getFinString(),
                        rows.getTariffFormula(),
                        rows.getDescription(),
                        request.getDateFrom(),
                        GenerateUtils.plusDate(request.getDateTo(), i + 179),
                        request.getSuppliers(),
                        rows.getRows()
                    ),
                    rows.getFilename()
                ));

            }
        }
        return files;
    }

    private GenerateLTLRows generateLTLRows(String fileName, String description, List<GenerateLTLRow> rows) {
        return new GenerateLTLRows(
                fileName,
                description,
                GenerateUtils.LOADING_UNIT_EP,
                GenerateUtils.COMMODITY_500,
                GenerateUtils.TARIFF_CODE_1000,
                rows
        );
    }

    private List<GenerateLTLRow> filterRailwayRows(Collection<LTLStandard> rows, String deliveryType, String consigneeType) {
        var mapRows = new ArrayList<GenerateLTLRow>();
        var firstList = new ArrayList<GenerateLTLRow>();
        var secondList = new ArrayList<GenerateLTLRow>();
        for (var row : rows) {
            if (deliveryType.equals(row.getDeliveryType()) && LOADING_UNIT_EP.equals(row.getLoadingUnit()) && consigneeType.equals(row.getConsigneeType())) {
                var city = citiesService.findByName(row.getUnloadingCity());
                if (city.isException()) {
                    addRowsByBodyType(firstList, row, city, city.getRZone());
                } else {
                    addRowsByBodyType(secondList, row, city, city.getCZone());
                    addRowsByBodyType(secondList, row, city, city.getRZone());
                }
            }
        }
        mapRows.addAll(firstList);
        mapRows.addAll(secondList);
        return mapRows;
    }

    private void addRowsByBodyType(List<GenerateLTLRow> secondList, LTLStandard row, CityModel city, String zone) {
        if (REF_TYPE.equals(row.getBodyType())) {
            secondList.add(addRow(row, city, "RSV", zone));
        } else {
            secondList.add(addRow(row, city, "CV", zone));
            secondList.add(addRow(row, city, "C20", zone));
            secondList.add(addRow(row, city, "C40", zone));
        }
    }

    private GenerateLTLRow addRow(LTLStandard row, CityModel city, String bodyType, String zone) {
        return new GenerateLTLRow(
            row.getCustomerWarehouse() == null || row.getCustomerWarehouse().equals(StringUtils.EMPTY)
                ? row.getWarehouseFm()
                : row.getCustomerWarehouse(),
            StringUtils.isNoneBlank(zone) ? zone : StringUtils.SPACE,
            city.isException() ? row.getUnloadingCity() : StringUtils.SPACE,
            CONSIGNEE_TYPE_RETAIL.equals(row.getConsigneeType()) ? CONSIGNEE_TYPE_RETAIL_EN : StringUtils.SPACE,
            bodyType,
            row.getDeliveryType(),
            row.getLoadingUnit(),
            row.getOne(),
            row.getTwo(),
            row.getThree(),
            row.getFour(),
            row.getFive(),
            row.getSixEight(),
            row.getNineFifteen(),
            row.getSixteenTwenty(),
            row.getTwentyOneTwentyFive()
        );
    }

    private List<String> generateLtl(LTLRequest request) {
        var files = new ArrayList<String>();
        var rowsList = new ArrayList<GenerateLTLRows>();
        if (request.isLtlReturn()) {
            log.info(GENERATE_WITH_RETURN_STARTED);
            rowsList.addAll(generateLTLStandards(request, true));
        }
        if (request.isLtlDelivery()) {
            log.info(GENERATE_WITHOUT_RETURN_STARTED);
            rowsList.addAll(generateLTLStandards(request, false));
        }
        for (int i = 0; i < rowsList.size(); i++) {
            var rows = rowsList.get(i);
            if (CollectionUtils.isNotEmpty(rows.getRows())) {
                files.add(generateService.generateTariffFile(
                    rows.getLoadingUnit().equals(LOADING_UNIT_LP) ? LTL_LP_TEMPLATE : LTL_STANDARD_TEMPLATE,
                    new GenerateLTLStandard(
                        request.getClientName(),
                        rows.getFinString(),
                        rows.getTariffFormula(),
                        rows.getDescription(),
                        request.getDateFrom(),
                        GenerateUtils.plusDate(request.getDateTo(), i + 1),
                        request.getSuppliers(),
                        rows.getRows()
                    ),
                    rows.getFilename()
                ));

            }
        }
        return files;
    }

    private List<GenerateLTLRows> generateLTLStandards(LTLRequest request, boolean isReturn) {
        var login = GenerateUtils.getLogin(request.getEmail());
        return List.of(
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_RTL_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_retail_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_RTL_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_retail_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_RTL_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_retail_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_RTL_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_retail_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_RTL_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_retail_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_RTL_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_retail_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_RTL_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети Мск TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_retail_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_RTL_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_alco_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, LT_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_alco_MSC_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое Мск alco TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, LT_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_retail_RGN_%s_%s", LT_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети TC", LT_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_RTL_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_retail_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_RTL_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_other_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP другое TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_EP),
                fileName(isReturn, "%s%s_EP_retail_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s EP сети TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_EP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_RTL_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_retail_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_RTL_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_other_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP другое TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_LP),
                fileName(isReturn, "%s%s_LP_retail_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s LP сети TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_LP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_RTL_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_retail_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_RTL_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_other_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP другое TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_HP),
                fileName(isReturn, "%s%s_HP_retail_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s HP сети TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_HP, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_RTL_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети Мск TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_retail_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_RTL_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_alco_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, ALCO_TYPE, MG_TYPE, true, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_alco_MSC_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое Мск alco TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_OTHER, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_other_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT другое TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            ),
            mapToGenerateModel(
                filterRows(request, FOOD_TYPES, MG_TYPE, false, CONSIGNEE_TYPE_RETAIL, LOADING_UNIT_OT),
                fileName(isReturn, "%s%s_OT_retail_RGN_%s_%s", MG_TYPE, request.getClientName(), login, request.getSuppliers()),
                description(isReturn, "%s OT сети TC", MG_TYPE),
                request.getLtlReturnType(), LOADING_UNIT_OT, request.getCommodityCode(), isReturn, request.isSurchargeOversize()
            )
        );
    }

    private String fileName(boolean isReturn, String fileName, String deliveryType, String clientName, String email, String suppliers) {
        return String.format(fileName, StringUtils.isNoneBlank(suppliers) ? PLM : StringUtils.EMPTY, isReturn ? RETURN_SIGN : deliveryType, clientName, email);
    }

    private String description(boolean isReturn, String description, String deliveryType) {
        return String.format(description, isReturn ? RETURN_SIGN : deliveryType);
    }

    private GenerateLTLRows mapToGenerateModel(
        List<LTLStandard> rows,
        String filename,
        String description,
        String returnType,
        String loadingUnit,
        String commodityCode,
        boolean isReturn,
        boolean isSurchargeOversize
    ) {
        var resultRows = new ArrayList<GenerateLTLRow>();
        var firstList = new ArrayList<GenerateLTLRow>();
        var secondList = new ArrayList<GenerateLTLRow>();
        var thirdList = new ArrayList<GenerateLTLRow>();
        for (var row : rows) {
            var unloadingCity = row.getUnloadingCity();
            log.info(UNLOADING_CITY_SET, unloadingCity);
            if (MOSCOW_ZONES_MAP.containsKey(unloadingCity)) {
                log.info(MOSCOW_ZONE_SET);
                resultRows.add(generateLTLRow(row, StringUtils.SPACE, MOSCOW_ZONES_MAP.get(unloadingCity), returnType, isReturn, isSurchargeOversize));
            } else {
                var city = citiesService.findByName(unloadingCity);
                log.info(CITY_FROM_DB_SET, city.getName());
                var satellites = citySatelliteService.citiesSatellites(unloadingCity);
                log.info(SATELLITES_CITIES_SET, satellites.toString());
                if (satellites.isEmpty()) {
                    if (city.isException()) {
                        log.info(CITY_DETECT_AS_EXCEPTION, city.getName());
                        firstList.add(generateLTLRow(row, unloadingCity, city.getRZone(), returnType, isReturn, isSurchargeOversize));
                    }
                    if (city.getRZone() == null && city.getCZone() != null) {
                        log.info(CITY_ADD_TO_SECOND_QUEUE, city.getName());
                        secondList.add(generateLTLRow(row, StringUtils.SPACE, city.getCZone(), returnType, isReturn, isSurchargeOversize));
                    }
                    if (city.getCZone() != null && city.getRZone() != null) {
                        log.info(CITY_ADD_TO_THIRD_QUEUE, city.getName());
                        thirdList.add(generateLTLRow(row, StringUtils.SPACE, city.getCZone(), returnType, isReturn, isSurchargeOversize));
                        thirdList.add(generateLTLRow(row, StringUtils.SPACE, city.getRZone(), returnType, isReturn, isSurchargeOversize));
                    }
                    log.info(PROCESSING_WITH_CITY_IS_COMPLETE, city.getName());
                } else {
                    for (var satellite : satellites) {
                        log.info(WORK_WITH_CITIES_SATELLITES);
                        if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                            log.info(CITY_SATELLITE_DETECT_AS_EXCEPTION, satellite.getSatellite());
                            firstList.add(generateLTLRow(row, satellite.getSatellite(), satellite.getRZone(), returnType, isReturn, isSurchargeOversize));
                        } else if (satellite.getCZone() != null) {
                            log.info(CITY_SATELLITE_ADD_TO_SECOND_QUEUE, satellite.getSatellite());
                            secondList.add(generateLTLRow(row, satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), satellite.getCZone(), returnType, isReturn, isSurchargeOversize));
                        } else {
                            log.info(CITY_SATELLITE_ADD_TO_THIRD_QUEUE, satellite.getSatellite());
                            thirdList.add(generateLTLRow(row, satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), satellite.getRZone(), returnType, isReturn, isSurchargeOversize));
                        }
                    }
                }
            }
        }
        resultRows.addAll(sortRowsByRef(firstList));
        resultRows.addAll(sortRowsByRef(secondList));
        resultRows.addAll(sortRowsByRef(thirdList));
        return new GenerateLTLRows(filename, description, loadingUnit, GenerateUtils.finString(commodityCode, isReturn), GenerateUtils.tariffFormula(commodityCode, isReturn), resultRows);
    }

    private GenerateLTLRow generateLTLRow(LTLStandard row, String unloadingCity, String zone, String returnType, boolean isReturn, boolean isSurchargeOversize) {
        return new GenerateLTLRow(
            row.getCustomerWarehouse() == null || row.getCustomerWarehouse().equals(StringUtils.EMPTY)
                ? row.getWarehouseFm()
                : row.getCustomerWarehouse(),
            zone,
            unloadingCity == null || unloadingCity.equals(StringUtils.EMPTY) || unloadingCity.equals(StringUtils.SPACE) ? StringUtils.SPACE : unloadingCity,
            row.getConsigneeType().equals(CONSIGNEE_TYPE_RETAIL) ? CONSIGNEE_TYPE_RETAIL_EN : StringUtils.SPACE,
            row.getBodyType().equals(TENT_TYPE) ? T_TYPE : row.getBodyType().equals(REF_TYPE) ? R_TYPE : I_TYPE,
            isReturn ? StringUtils.SPACE : row.getDeliveryType(),
            row.getLoadingUnit(),
            costMapping(row, returnType, isReturn, row.getOne(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getTwo(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getThree(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getFour(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getFive(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getSixEight(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getNineFifteen(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getSixteenTwenty(), isSurchargeOversize),
            costMapping(row, returnType, isReturn, row.getTwentyOneTwentyFive(), isSurchargeOversize)
        );
    }

    private String costMapping(LTLStandard row, String returnType, boolean isReturn, String cost, boolean isSurchargeOversize) {
        if (cost != null && !cost.equals(StringUtils.EMPTY)) {
            if (isReturn && MARGIN_TYPE.equals(returnType)) {
                return GenerateUtils.margin(cost);
            } else if (LOADING_UNIT_OT.equals(row.getLoadingUnit()) && isSurchargeOversize && !isReturn) {
                return GenerateUtils.margin(cost);
            } else {
                return cost;
            }
        }
        return cost;
    }

    private List<LTLStandard> filterRows(
        LTLRequest request,
        List<String> codes,
        String deliveryType,
        boolean moscow,
        String consigneeType,
        String loadingUnit) {
        for (var code : codes) {
            if (code.equals(request.getCommodityCode())) {
                var rows = request.getStandards();
                return moscow
                    ? rows.stream()
                    .filter(row -> deliveryType.equals(row.getDeliveryType()))
                    .filter(row -> consigneeType.equals(row.getConsigneeType()))
                    .filter(row -> loadingUnit.equals(row.getLoadingUnit()))
                    .filter(row ->
                        MOSCOW_FIRST_ZONE.equals(row.getUnloadingCity())
                            || MOSCOW_SECOND_ZONE.equals(row.getUnloadingCity())
                            || MOSCOW_THIRD_ZONE.equals(row.getUnloadingCity())
                            || MOSCOW_FOURTH_ZONE.equals(row.getUnloadingCity()))
                    .collect(Collectors.toList())
                    : rows.stream()
                    .filter(row -> deliveryType.equals(row.getDeliveryType()))
                    .filter(row -> consigneeType.equals(row.getConsigneeType()))
                    .filter(row -> loadingUnit.equals(row.getLoadingUnit()))
                    .filter(row ->
                        !MOSCOW_FIRST_ZONE.equals(row.getUnloadingCity())
                            && !MOSCOW_SECOND_ZONE.equals(row.getUnloadingCity())
                            && !MOSCOW_THIRD_ZONE.equals(row.getUnloadingCity())
                            && !MOSCOW_FOURTH_ZONE.equals(row.getUnloadingCity())
                    ).collect(Collectors.toList());
            }
        }
        return List.of();
    }


    private DowntimeModel mapRequestToDowntimeModel(LTLRequest request) {
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

    private OverweightModel mapRequestToOverweightModel(LTLRequest request) {
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

    private PrrModel mapRequestToPrrModel(LTLRequest request) {
        return new PrrModel(
            request.getType(),
            request.getClientName(),
            request.getEmail(),
            request.getDateFrom(),
            request.getDateTo(),
            request.getSuppliers(),
            request.getPrr()
        );
    }

    private List<GenerateLTLRow> sortRowsByRef(List<GenerateLTLRow> rows) {
        return rows.stream().sorted((o1, o2) -> {
            if (o1.getBodyType().equals(R_TYPE) && !o2.getBodyType().equals(R_TYPE)) {
                return -1;
            } else if (!o1.getBodyType().equals(R_TYPE) && o2.getBodyType().equals(R_TYPE)) {
                return 1;
            } else {
                return 0;
            }
        }).toList();
    }
}
