package com.fmlogistic.tariffcreator.services.forms.additional;

import com.fmlogistic.tariffcreator.models.generator.additional.AddPointFTLRow;
import com.fmlogistic.tariffcreator.models.generator.additional.FTLAdditionalModel;
import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeFTLRow;
import com.fmlogistic.tariffcreator.models.generator.downtime.ForwardingFTL;
import com.fmlogistic.tariffcreator.models.generator.downtime.ForwardingFTLRow;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateAddPointFTL;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateAdditionalDowntimeFTL;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateAdditionalPointFTL;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateDowntimeFTL;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateForwardingFTL;
import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
import com.fmlogistic.tariffcreator.utils.GenerateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_103;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_104;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_201;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_210;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_211;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.FT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.I_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.MOSCOW_LOADING_LOCATION;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.REGION_LOADING_LOCATION;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.R_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_10003;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1099;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TYPE_MAPPER;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.T_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_MOSCOW;

@Service
@RequiredArgsConstructor
public class FTLAdditionalServiceImpl implements FTLAdditionalService {

    private static final String OVER_24_HOURS_DOWNTIME_SERVICE_NAME = "Стоимость за каждые сутки простоя транспортного средства при превышении первых 24 часов работы";
    private static final String ADDITIONAL_UNLOADING_POINT_SERVICE_NAME = "Дополнительная точка выгрузки в городе";
    private static final String ADDITIONAL_UNLOADING_POINT_NOT_MORE_100_SERVICE_NAME = "Дополнительная точка выгрузки по пути следования, но не более 100 км от маршрута";
    private static final String DRIVER_FORWARDING_SERVICE_NAME = "Экспедирование водителем (покоробочная приемка и сдача груза)";
    private static final String FM_FORWARDER_SERVICE_NAME = "Экспедитор ФМ Ложистик на загрузке/выгрузке";

    private static final String ADDITIONAL_DOWNTIME_TEMPLATE_NAME = "additional-downtime-ftl";
    private static final String ADDITIONAL_ADD_POINT_TEMPLATE_NAME = "additional-addpoint";
    private static final String ADDITIONAL_FORWARDING_TEMPLATE_NAME = "additional-forwarding-ftl";

    private static final String ADDITIONAL_DOWNTIME_MR_FILE_NAME = "FTL_DownTime_MR_%s_%s";
    private static final String ADDITIONAL_DOWNTIME_RM_FILE_NAME = "FTL_DownTime_RM_%s_%s";
    private static final String ADDITIONAL_ADD_POINT_MR_FILE_NAME = "FTL_AddPoint_city_MR_%s_%s";
    private static final String ADDITIONAL_ADD_POINT_RM_FILE_NAME = "FTL_AddPoint_city_RM_%s_%s";
    private static final String ADDITIONAL_ADD_POINT_NON_MR_FILE_NAME = "FTL_AddPoint_non_MR_%s_%s";
    private static final String ADDITIONAL_ADD_POINT_NON_RM_FILE_NAME = "FTL_AddPoint_non_RM_%s_%s";
    private static final String ADDITIONAL_FORWARDING_MR_FILE_NAME = "FTL_Exp_Driver_MR_%s_%s";
    private static final String ADDITIONAL_FORWARDING_RM_FILE_NAME = "FTL_Exp_Driver_RM_%s_%s";
    private static final String ADDITIONAL_FORWARDING_BY_FM_MR_FILE_NAME = "FTL_Exp_by_FM_MR_%s_%s";
    private static final String ADDITIONAL_FORWARDING_BY_FM_RM_FILE_NAME = "FTL_Exp_by_FM_RM_%s_%s";

    private static final String ADDITIONAL_DOWNTIME_MR_DESCRIPTION = "FT MR простой TC";
    private static final String ADDITIONAL_DOWNTIME_RM_DESCRIPTION = "FT RM простой TC";
    private static final String ADDITIONAL_ADD_POINT_MR_DESCRIPTION = "FT MR гор. доп. точка TC";
    private static final String ADDITIONAL_ADD_POINT_RM_DESCRIPTION = "FT RM гор. доп. точка TC";
    private static final String ADDITIONAL_ADD_POINT_NON_MR_DESCRIPTION = "FT MR внегор. доп. точка TC";
    private static final String ADDITIONAL_ADD_POINT_NON_RM_DESCRIPTION = "FT RM внегор. доп. точка TC";
    private static final String ADDITIONAL_FORWARDING_MR_DESCRIPTION = "FT MR эксп. водит FM TC";
    private static final String ADDITIONAL_FORWARDING_RM_DESCRIPTION = "FT RM эксп. водит FM TC";
    private static final String ADDITIONAL_FORWARDING_BY_FM_MR_DESCRIPTION = "FT MR эксп-е ФМ TT";
    private static final String ADDITIONAL_FORWARDING_BY_RM_MR_DESCRIPTION = "FT RM эксп-е ФМ TT";

    private final GenerateService generateService;

    @Override
    public List<String> generateAdditional(FTLRequest request) {
        var filesName = new ArrayList<String>();
        var login = GenerateUtils.getLogin(request.getEmail());
        for (var model : generateAdditionalDowntimeFTL(request, login)) {
            if (CollectionUtils.isNotEmpty(model.getGenerateRows().getRows())) {
                filesName.add(generateService.generateTariffFile(model.getTemplate(), model.getGenerateRows(), model.getFilename()));
            }
        }
        for (var model : additionalPointFTL(request, login)) {
            if (CollectionUtils.isNotEmpty(model.getGenerateRows().getRows())) {
                filesName.add(generateService.generateTariffFile(model.getTemplate(), model.getGenerateRows(), model.getFilename()));
            }
        }
        for (var model : generateForwardingFTL(request, login)) {
            if (CollectionUtils.isNotEmpty(model.getGenerateRows().getRows())) {
                filesName.add(generateService.generateTariffFile(model.getTemplate(), model.getGenerateRows(), model.getFilename()));
            }
        }
        return filesName;
    }

    private List<GenerateAdditionalDowntimeFTL> generateAdditionalDowntimeFTL(FTLRequest request, String login) {
        var downtimeMR = new GenerateAdditionalDowntimeFTL(
            ADDITIONAL_DOWNTIME_TEMPLATE_NAME,
            String.format(ADDITIONAL_DOWNTIME_MR_FILE_NAME, request.getClientName(), login),
            new GenerateDowntimeFTL(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 169),
                COMMODITY_201,
                TARIFF_CODE_1099,
                ADDITIONAL_DOWNTIME_MR_DESCRIPTION,
                generateDowntimeFTLRows(request.getAdditional(), MOSCOW_LOADING_LOCATION)
            ));
        var downtimeRM = new GenerateAdditionalDowntimeFTL(
            ADDITIONAL_DOWNTIME_TEMPLATE_NAME,
            String.format(ADDITIONAL_DOWNTIME_RM_FILE_NAME, request.getClientName(), login),
            new GenerateDowntimeFTL(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 170),
                COMMODITY_201,
                TARIFF_CODE_1099,
                ADDITIONAL_DOWNTIME_RM_DESCRIPTION,
                generateDowntimeFTLRows(request.getAdditional(), REGION_LOADING_LOCATION)
            )
        );
        return List.of(downtimeMR, downtimeRM);
    }

    private List<GenerateAdditionalPointFTL> additionalPointFTL(FTLRequest request, String login) {
        return List.of(
            new GenerateAdditionalPointFTL(
                ADDITIONAL_ADD_POINT_TEMPLATE_NAME,
                String.format(ADDITIONAL_ADD_POINT_MR_FILE_NAME, request.getClientName(), login),
                new GenerateAddPointFTL(
                    request.getClientName(),
                    request.getDateFrom(),
                    GenerateUtils.plusDate(request.getDateTo(), 171),
                    COMMODITY_103,
                    TARIFF_CODE_10003,
                    ADDITIONAL_ADD_POINT_MR_DESCRIPTION,
                    addPointFTLRows(request.getAdditional(), ADDITIONAL_UNLOADING_POINT_SERVICE_NAME,
                        MOSCOW_LOADING_LOCATION)
                )
            ),
            new GenerateAdditionalPointFTL(
                ADDITIONAL_ADD_POINT_TEMPLATE_NAME,
                String.format(ADDITIONAL_ADD_POINT_RM_FILE_NAME, request.getClientName(), login),
                new GenerateAddPointFTL(
                    request.getClientName(),
                    request.getDateFrom(),
                    GenerateUtils.plusDate(request.getDateTo(), 172),
                    COMMODITY_103,
                    TARIFF_CODE_10003,
                    ADDITIONAL_ADD_POINT_RM_DESCRIPTION,
                    addPointFTLRows(request.getAdditional(), ADDITIONAL_UNLOADING_POINT_SERVICE_NAME,
                        REGION_LOADING_LOCATION)
                )
            ),
            new GenerateAdditionalPointFTL(
                ADDITIONAL_ADD_POINT_TEMPLATE_NAME,
                String.format(ADDITIONAL_ADD_POINT_NON_MR_FILE_NAME, request.getClientName(), login),
                new GenerateAddPointFTL(
                    request.getClientName(),
                    request.getDateFrom(),
                    GenerateUtils.plusDate(request.getDateTo(), 173),
                    COMMODITY_104,
                    TARIFF_CODE_10003,
                    ADDITIONAL_ADD_POINT_NON_MR_DESCRIPTION,
                    addPointFTLRows(request.getAdditional(), ADDITIONAL_UNLOADING_POINT_NOT_MORE_100_SERVICE_NAME,
                        MOSCOW_LOADING_LOCATION)
                )
            ),
            new GenerateAdditionalPointFTL(
                ADDITIONAL_ADD_POINT_TEMPLATE_NAME,
                String.format(ADDITIONAL_ADD_POINT_NON_RM_FILE_NAME, request.getClientName(), login),
                new GenerateAddPointFTL(
                    request.getClientName(),
                    request.getDateFrom(),
                    GenerateUtils.plusDate(request.getDateTo(), 174),
                    COMMODITY_104,
                    TARIFF_CODE_10003,
                    ADDITIONAL_ADD_POINT_NON_RM_DESCRIPTION,
                    addPointFTLRows(request.getAdditional(), ADDITIONAL_UNLOADING_POINT_NOT_MORE_100_SERVICE_NAME,
                        REGION_LOADING_LOCATION)
                )
            )
        );
    }

    private List<GenerateForwardingFTL> generateForwardingFTL(FTLRequest request, String login) {
        var ftlExpDriverMR = new GenerateForwardingFTL(
            ADDITIONAL_FORWARDING_TEMPLATE_NAME,
            String.format(ADDITIONAL_FORWARDING_MR_FILE_NAME, request.getClientName(), login),
            new ForwardingFTL(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 175),
                COMMODITY_211,
                TARIFF_CODE_1099,
                ADDITIONAL_FORWARDING_MR_DESCRIPTION,
                forwardingFTLRows(request.getAdditional(), DRIVER_FORWARDING_SERVICE_NAME, MOSCOW_LOADING_LOCATION)
            )
        );
        var ftlExpDriverRM = new GenerateForwardingFTL(
            ADDITIONAL_FORWARDING_TEMPLATE_NAME,
            String.format(ADDITIONAL_FORWARDING_RM_FILE_NAME, request.getClientName(), login),
            new ForwardingFTL(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 176),
                COMMODITY_211,
                TARIFF_CODE_1099,
                ADDITIONAL_FORWARDING_RM_DESCRIPTION,
                forwardingFTLRows(request.getAdditional(), DRIVER_FORWARDING_SERVICE_NAME, REGION_LOADING_LOCATION)
            )
        );
        var ftlExpDByFmMR = new GenerateForwardingFTL(
            ADDITIONAL_FORWARDING_TEMPLATE_NAME,
            String.format(ADDITIONAL_FORWARDING_BY_FM_MR_FILE_NAME, request.getClientName(), login),
            new ForwardingFTL(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 177),
                COMMODITY_210,
                TARIFF_CODE_1099,
                ADDITIONAL_FORWARDING_BY_FM_MR_DESCRIPTION,
                forwardingFTLRows(request.getAdditional(), FM_FORWARDER_SERVICE_NAME, MOSCOW_LOADING_LOCATION)
            )
        );
        var ftlExpDByFmRM = new GenerateForwardingFTL(
            ADDITIONAL_FORWARDING_TEMPLATE_NAME,
            String.format(ADDITIONAL_FORWARDING_BY_FM_RM_FILE_NAME, request.getClientName(), login),
            new ForwardingFTL(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 178),
                COMMODITY_210,
                TARIFF_CODE_1099,
                ADDITIONAL_FORWARDING_BY_RM_MR_DESCRIPTION,
                forwardingFTLRows(request.getAdditional(), FM_FORWARDER_SERVICE_NAME, REGION_LOADING_LOCATION)
            )
        );
        return List.of(ftlExpDriverMR, ftlExpDriverRM, ftlExpDByFmMR, ftlExpDByFmRM);
    }

    private List<ForwardingFTLRow> forwardingFTLRows(List<FTLAdditionalModel> intercity, String serviceType, String loadingLocation) {
        var rows = new ArrayList<ForwardingFTLRow>();
        for (var row : intercity) {
            if (loadingLocation.equals(row.getLoadingLocation())) {
                if (row.getServiceName().equals(serviceType)) {
                    if (row.getTent() != null && !row.getTent().equals(StringUtils.EMPTY)) {
                        rows.add(forwardingFTLRow(row.getVehicleType(), T_TYPE, row.getTent()));
                    }
                    if (row.getIzoterm() != null && !row.getIzoterm().equals(StringUtils.EMPTY)) {
                        rows.add(forwardingFTLRow(row.getVehicleType(), I_TYPE, row.getIzoterm()));
                    }
                    if (row.getRef() != null && !row.getRef().equals(StringUtils.EMPTY)) {
                        rows.add(forwardingFTLRow(row.getVehicleType(), R_TYPE, row.getRef()));
                    }
                }
            }
        }
        return rows;
    }

    private List<DowntimeFTLRow> generateDowntimeFTLRows(List<FTLAdditionalModel> intercity, String loadingLocation) {
        var rows = new ArrayList<DowntimeFTLRow>();
        for (var row : intercity) {
            if (loadingLocation.equals(row.getLoadingLocation())) {
                if (OVER_24_HOURS_DOWNTIME_SERVICE_NAME.equals(row.getServiceName())) {
                    var validTent = validateRate(row.getTent());
                    if (validTent != null) {
                        rows.addAll(generateDowntimeRows(row.getVehicleType(), T_TYPE, validTent));
                    }
                    var validIzoterm = validateRate(row.getIzoterm());
                    if (validIzoterm != null) {
                        rows.addAll(generateDowntimeRows(row.getVehicleType(), I_TYPE, validIzoterm));
                    }
                    var validRef = validateRate(row.getRef());
                    if (validRef != null) {
                        rows.addAll(generateDowntimeRows(row.getVehicleType(), R_TYPE, validRef));
                    }
                }
            }
        }
        return rows;
    }

    private List<AddPointFTLRow> addPointFTLRows(List<FTLAdditionalModel> addPoints, String serviceType, String loadingLocation) {
        var rows = new ArrayList<AddPointFTLRow>();
        for (var row : addPoints) {
            if (loadingLocation.equals(row.getLoadingLocation())) {
                if (row.getServiceName().equals(serviceType)) {
                    if (row.getTent() != null && !row.getTent().equals(StringUtils.EMPTY)) {
                        rows.add(generateAddPointRow(row.getVehicleType(), T_TYPE, row.getTent()));
                    }
                    if (row.getIzoterm() != null && !row.getIzoterm().equals(StringUtils.EMPTY)) {
                        rows.add(generateAddPointRow(row.getVehicleType(), I_TYPE, row.getIzoterm()));
                    }
                    if (row.getRef() != null && !row.getRef().equals(StringUtils.EMPTY)) {
                        rows.add(generateAddPointRow(row.getVehicleType(), R_TYPE, row.getRef()));
                    }
                }
            }
        }
        return rows;
    }

    private String validateRate(String rate) {
        if (rate != null && !rate.equals(StringUtils.EMPTY) && !rate.equals("0")) {
            return rate;
        }
        return null;
    }

    private List<DowntimeFTLRow> generateDowntimeRows(String vehicleType, String bodyType, String rate) {
        var rows = new ArrayList<DowntimeFTLRow>();
        var hours = 24;
        for (int i = 0; i <= 9; i++) {
            rows.add(
                new DowntimeFTLRow(
                    ZONE_MOSCOW,
                    ZONE_MOSCOW,
                    String.format(TYPE_MAPPER, bodyType, mapTruckType(vehicleType)),
                    String.valueOf(hours * i),
                    String.valueOf(hours * (i + 1)),
                    FT_TYPE,
                    String.valueOf(Integer.parseInt(rate) * i)
                )
            );
        }
        return rows;
    }

    private AddPointFTLRow generateAddPointRow(String vehicleType, String bodyType, String rate) {
        return new AddPointFTLRow(
            ZONE_MOSCOW,
            ZONE_MOSCOW,
            String.format(TYPE_MAPPER, bodyType, mapTruckType(vehicleType)),
            FT_TYPE,
            String.valueOf(Integer.parseInt(rate) * 0.001),
            rate
        );
    }

    private ForwardingFTLRow forwardingFTLRow(String vehicleType, String bodyType, String rate) {
        return new ForwardingFTLRow(
            ZONE_MOSCOW,
            ZONE_MOSCOW,
            String.format(TYPE_MAPPER, bodyType, mapTruckType(vehicleType)),
            FT_TYPE,
            rate
        );
    }

    private String mapTruckType(String truckType) {
        return truckType.substring(0, truckType.length() - 1);
    }

}