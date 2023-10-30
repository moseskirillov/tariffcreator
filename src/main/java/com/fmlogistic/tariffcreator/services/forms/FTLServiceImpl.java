package com.fmlogistic.tariffcreator.services.forms;

import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalPointFTL;
import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalPointFTLRows;
import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalPointsFTLRow;
import com.fmlogistic.tariffcreator.models.generator.additional.ForwardingFTL;
import com.fmlogistic.tariffcreator.models.generator.additional.ForwardingFTLRow;
import com.fmlogistic.tariffcreator.models.generator.additional.ForwardingFTLRows;
import com.fmlogistic.tariffcreator.models.generator.additional.LoadingUnloadingFTL;
import com.fmlogistic.tariffcreator.models.generator.additional.LoadingUnloadingFTLRow;
import com.fmlogistic.tariffcreator.models.generator.additional.LoadingUnloadingFTLRows;
import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeMscRow;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateDowntimeMscRow;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateDowntimeMscRows;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateFTLDowntimeMSC;
import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;
import com.fmlogistic.tariffcreator.models.generator.standard.FTLMoscow;
import com.fmlogistic.tariffcreator.models.generator.standard.FTLMoscowRowCapacity;
import com.fmlogistic.tariffcreator.models.generator.standard.FTLMoscowRowTransport;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateFTLMSCDeliveryCapacity;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateFTLMSCDeliveryTransport;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateFTLMoscow;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateFTLRowsCapacity;
import com.fmlogistic.tariffcreator.models.generator.standard.GenerateFTLRowsTransport;
import com.fmlogistic.tariffcreator.services.forms.additional.FTLAdditionalService;
import com.fmlogistic.tariffcreator.services.forms.intercity.IntercityService;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
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
import java.util.List;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.CF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_102;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_103;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_107;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_113;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_201;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_202;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_211;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.FTL_FORM_NAME;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.FT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_10003;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_10018;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_10099;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1099;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1399;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TYPE_MAPPER;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZERO_STRING;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_1;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_2;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_3;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_4;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_MOSCOW;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.getLogin;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.mapBodyType;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.mapFileName;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.mapTruckType;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FTLServiceImpl implements FTLService {

    private static final String TWELVE = "12";

    private static final String TC_DELIVERY_TYPE = "TC";
    private static final String CAPACITY_DELIVERY_TYPE = "capacity";

    private static final String DESCRIPTION_BY_TRANSPORT_TYPE = "%s Мск по типу транспорта TC";
    private static final String DESCRIPTION_ADDITIONAL_POINT = "%s доп. точка по Мск TC";
    private static final String DESCRIPTION_MOSCOW_FORWARDING = "FT экспедирование по Мск ТТ";
    private static final String DESCRIPTION_MOSCOW_PRR = "FT ПРР по Мск ТС";
    private static final String DESCRIPTION_MOSCOW_DOWNTIME = "FT простой по Мск до ТС";

    private static final Double ADDITIONAL_POINT_MULTIPLIER = 0.001;

    private static final String FILENAME_MOSCOW_DOWNTIME = "%s_downtime_MM_%s_%s";
    private static final String FILENAME_MOSCOW_FORWARDING = "FT_expedition_MM_%s_%s";
    private static final String FILENAME_MOSCOW_PRR = "FT_prr_MM_%s_%s";
    private static final String FILENAME_MOSCOW_DELIVERY = "%s_delivery_MM_truck_%s_%s";
    private static final String FILENAME_MOSCOW_ADDITIONAL_POINT = "%s_addpoint_MM_%s_%s";
    private static final String FILENAME_MOSCOW_CAPACITY = "%s_delivery_MM_capacity_%s_%s";

    private static final String TEMPLATE_MOSCOW_CAPACITY = "ftl-moscow-capacity";
    private static final String TEMPLATE_MOSCOW_DOWNTIME = "ftl-moscow-downtime";
    private static final String TEMPLATE_MOSCOW_PRR = "ftl-prr";
    private static final String TEMPLATE_MOSCOW_FORWARDING = "ftl-forwarding";
    private static final String TEMPLATE_MOSCOW_ADDITIONAL_POINT = "ftl-addpoint";
    private static final String TEMPLATE_MOSCOW_TRANSPORT = "ftl-moscow-transport";

    private static final String GENERATE_STARTED = "Началась генерация FTL в потоке {}";
    private static final String GENERATE_MSC_STARTED = "Началась генерация FTL Москва";
    private static final String GENERATE_BY_TRANSPORT = "Началась генерация по типу ТС";
    private static final String GENERATE_BY_CAPACITY = "Началась генерация по вместимости";
    private static final String GENERATE_FTL_DOWNTIME = "Началась генерация простоя";
    private static final String GENERATE_FTL_ADD_POINT = "Началась генерация доп. точки";
    private static final String GENERATE_FTL_LOAD_UNLOAD = "Началась генерация ПРР";
    private static final String GENERATE_FTL_FORWARDING = "Началась генерация экспедирования";

    private static final String GENERATE_ERROR_MESSAGE = "Произошла ошибка генерации файла FTL:";

    private final UnisenderService unisenderService;
    private final GenerateService generateService;
    private final IntercityService intercityService;
    private final FTLAdditionalService additionalService;
    private final CommentFilesService commentFilesService;

    @Async
    @Override
    public void generate(FTLRequest request) {
        try {
            log.info(GENERATE_STARTED, Thread.currentThread().getName());
            var files = new ArrayList<String>();
            var result = generateMsk(request);
            var intercityResult = intercityService.generate(request);
            var additionalResult = additionalService.generateAdditional(request);
            var downtimeRows = result.getDowntimeMscRows();
            var loadingUnloading = result.getLoadingUnloadingFTLRows();
            var forwarding = result.getForwardingFTLRows();
            for (var rows : result.getMscDeliveryTCS()) {
                if (!rows.getRows().getFirstZoneRows().isEmpty()
                    || !rows.getRows().getSecondZoneRows().isEmpty()
                    || !rows.getRows().getThirdZoneRows().isEmpty()
                    || !rows.getRows().getFourthZoneRows().isEmpty()) {
                    files.add(generateService.generateTariffFile(rows.getTemplate(), rows.getRows(), rows.getFileName()));
                }
            }
            for (var rows : result.getMscDeliveryCapacity()) {
                if (!rows.getRows().getFirstZoneRows().isEmpty()
                    || !rows.getRows().getSecondZoneRows().isEmpty()
                    || !rows.getRows().getThirdZoneRows().isEmpty()
                    || !rows.getRows().getFourthZoneRows().isEmpty()) {
                    files.add(generateService.generateTariffFile(rows.getTemplate(), rows.getRows(), rows.getFileName()));
                }
            }
            for (var rows : result.getAdditionalPointFTLRows()) {
                if (!rows.getRows().getRows().isEmpty()) {
                    files.add(generateService.generateTariffFile(rows.getTemplate(), rows.getRows(), rows.getFileName()));
                }
            }
            if (!downtimeRows.getDowntimeMscRows().getFirstZoneRows().isEmpty()
                || !downtimeRows.getDowntimeMscRows().getSecondZoneRows().isEmpty()
                || !downtimeRows.getDowntimeMscRows().getThirdZoneRows().isEmpty()
                || !downtimeRows.getDowntimeMscRows().getFourthZoneRows().isEmpty()) {
                files.add(generateService.generateTariffFile(downtimeRows.getTemplate(), downtimeRows.getDowntimeMscRows(), downtimeRows.getFileName()));
            }
            if (!loadingUnloading.getRows().getRows().isEmpty()) {
                files.add(generateService.generateTariffFile(loadingUnloading.getTemplate(), loadingUnloading.getRows(), loadingUnloading.getFileName()));
            }
            if (!forwarding.getRows().getRows().isEmpty()) {
                files.add(generateService.generateTariffFile(forwarding.getTemplate(), forwarding.getRows(), forwarding.getFileName()));
            }
            files.addAll(intercityResult);
            files.addAll(additionalResult);
            files.forEach(e -> commentFilesService.saveOrUpdate(e, request.getComment()));
            unisenderService.sendCreationMessage(request.getEmail(), FTL_FORM_NAME, request.getClientName(), files, request.getDateFrom(), request.getDateTo(), request.getComment());
        } catch (Exception e) {
            log.error(GENERATE_ERROR_MESSAGE, e);
            unisenderService.sendErrorMessage(request.getEmail(), request.getClientName(), request.getType());
            throw e;
        }
    }

    private GenerateFTLMoscow generateMsk(FTLRequest request) {
        log.info(GENERATE_MSC_STARTED);
        var login = getLogin(request.getEmail());
        return new GenerateFTLMoscow(
            generateFTLMSCDeliveryTransports(request, login),
            generateFTLMSCDeliveryCapacities(request, login),
            generateFTLDowntimeMSC(request, login),
            additionalPointFTLS(request, login),
            loadingUnloading(request, login),
            forwardingFTL(request, login)
        );
    }

    private List<GenerateFTLMSCDeliveryTransport> generateFTLMSCDeliveryTransports(FTLRequest request, String login) {
        log.info(GENERATE_BY_TRANSPORT);
        var deliveryTransport = new ArrayList<GenerateFTLMSCDeliveryTransport>();
        if (TC_DELIVERY_TYPE.equals(request.getBilling())) {
            deliveryTransport.add(new GenerateFTLMSCDeliveryTransport(
                TEMPLATE_MOSCOW_TRANSPORT,
                mapFileName(FILENAME_MOSCOW_DELIVERY, FT_TYPE, request.getClientName(), login),
                generateFTLRowsTransport(request, COMMODITY_102, TARIFF_CODE_1099, FT_TYPE, 181)
            ));
            deliveryTransport.add(new GenerateFTLMSCDeliveryTransport(
                TEMPLATE_MOSCOW_TRANSPORT,
                mapFileName(FILENAME_MOSCOW_DELIVERY, CF_TYPE, request.getClientName(), login),
                generateFTLRowsTransport(request, COMMODITY_107, TARIFF_CODE_1399, CF_TYPE, 182)
            ));
        }
        return deliveryTransport;
    }

    private GenerateFTLRowsTransport generateFTLRowsTransport(FTLRequest request, String activity, String tariffCode, String type, int plusDate) {
        return new GenerateFTLRowsTransport(
            request.getClientName(),
            request.getDateFrom(),
            GenerateUtils.plusDate(request.getDateTo(), plusDate),
            activity,
            tariffCode,
            String.format(DESCRIPTION_BY_TRANSPORT_TYPE, type),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneOne()))
                .map(e -> ftlMoscowRowTransport(e, ZONE_1, e.getZoneOne(), type))
                .toList(),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneTwo()))
                .map(e -> ftlMoscowRowTransport(e, ZONE_2, e.getZoneTwo(), type))
                .toList(),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneThree()))
                .map(e -> ftlMoscowRowTransport(e, ZONE_3, e.getZoneThree(), type))
                .toList(),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneFour()))
                .map(e -> ftlMoscowRowTransport(e, ZONE_4, e.getZoneFour(), type))
                .toList()
        );
    }

    private FTLMoscowRowTransport ftlMoscowRowTransport(FTLMoscow row, String zone, String rate, String type) {
        return new FTLMoscowRowTransport(
            row.getLoadingLocation(),
            zone,
            String.format(TYPE_MAPPER, mapBodyType(row.getBodyType()), mapTruckType(row.getTruckType())),
            rate,
            type
        );
    }

    private List<GenerateFTLMSCDeliveryCapacity> generateFTLMSCDeliveryCapacities(FTLRequest request, String login) {
        log.info(GENERATE_BY_CAPACITY);
        var deliveryCapacity = new ArrayList<GenerateFTLMSCDeliveryCapacity>();
        if (CAPACITY_DELIVERY_TYPE.equals(request.getBilling())) {
            deliveryCapacity.add(new GenerateFTLMSCDeliveryCapacity(
                TEMPLATE_MOSCOW_CAPACITY,
                mapFileName(FILENAME_MOSCOW_CAPACITY, FT_TYPE, request.getClientName(), login),
                generateFTLRowsCapacity(request, COMMODITY_102, TARIFF_CODE_1099, FT_TYPE, 179)
            ));
            deliveryCapacity.add(new GenerateFTLMSCDeliveryCapacity(
                TEMPLATE_MOSCOW_CAPACITY,
                mapFileName(FILENAME_MOSCOW_CAPACITY, CF_TYPE, request.getClientName(), login),
                generateFTLRowsCapacity(request, COMMODITY_107, TARIFF_CODE_1399, CF_TYPE, 180)
            ));
        }
        return deliveryCapacity;
    }

    private GenerateFTLRowsCapacity generateFTLRowsCapacity(FTLRequest request, String activity, String tariffCode, String type, int plusDate) {
        return new GenerateFTLRowsCapacity(
            request.getClientName(),
            request.getDateFrom(),
            GenerateUtils.plusDate(request.getDateTo(), plusDate),
            activity,
            tariffCode,
            String.format(DESCRIPTION_BY_TRANSPORT_TYPE, type),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneOne()))
                .map(e -> ftlMoscowRowCapacity(e, ZONE_1, type, e.getZoneOne()))
                .toList(),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneTwo()))
                .map(e -> ftlMoscowRowCapacity(e, ZONE_2, type, e.getZoneTwo()))
                .toList(),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneThree()))
                .map(e -> ftlMoscowRowCapacity(e, ZONE_3, type, e.getZoneThree()))
                .toList(),
            request.getMoscow()
                .stream()
                .filter(e -> requireNonNull(e.getZoneFour()))
                .map(e -> ftlMoscowRowCapacity(e, ZONE_4, type, e.getZoneFour()))
                .toList()
        );
    }

    private GenerateFTLDowntimeMSC generateFTLDowntimeMSC(FTLRequest request, String login) {
        log.info(GENERATE_FTL_DOWNTIME);
        return new GenerateFTLDowntimeMSC(
            TEMPLATE_MOSCOW_DOWNTIME,
            mapFileName(FILENAME_MOSCOW_DOWNTIME, FT_TYPE, request.getClientName(), login),
            new GenerateDowntimeMscRows(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 183),
                COMMODITY_201,
                TARIFF_CODE_1099,
                DESCRIPTION_MOSCOW_DOWNTIME,
                request.getMoscow()
                    .stream()
                    .filter(e -> StringUtils.isNoneBlank(e.getDowntime()))
                    .filter(e -> requireNonNull(e.getZoneOne()))
                    .map(e -> generateDowntimeMscRow(e, ZONE_1, e.getZoneOne(), e.getDowntime()))
                    .toList(),
                request.getMoscow()
                    .stream()
                    .filter(e -> StringUtils.isNoneBlank(e.getDowntime()))
                    .filter(e -> requireNonNull(e.getZoneTwo()))
                    .map(e -> generateDowntimeMscRow(e, ZONE_2, e.getZoneTwo(), e.getDowntime()))
                    .toList(),
                request.getMoscow()
                    .stream()
                    .filter(e -> StringUtils.isNoneBlank(e.getDowntime()))
                    .filter(e -> requireNonNull(e.getZoneThree()))
                    .map(e -> generateDowntimeMscRow(e, ZONE_3, e.getZoneThree(), e.getDowntime()))
                    .toList(),
                request.getMoscow()
                    .stream()
                    .filter(e -> StringUtils.isNoneBlank(e.getDowntime()))
                    .filter(e -> requireNonNull(e.getZoneFour()))
                    .map(e -> generateDowntimeMscRow(e, ZONE_4, e.getZoneFour(), e.getDowntime()))
                    .toList()
            ));
    }

    private GenerateDowntimeMscRow generateDowntimeMscRow(FTLMoscow row, String zone, String rate, String downtimeRate) {
        return new GenerateDowntimeMscRow(
            zone,
            String.format(TYPE_MAPPER, mapBodyType(row.getBodyType()), mapTruckType(row.getTruckType())),
            FT_TYPE,
            generateDowntimeMscRows(rate, downtimeRate)
        );
    }

    private List<DowntimeMscRow> generateDowntimeMscRows(String deliveryRate, String downtimeRate) {
        var rows = new ArrayList<DowntimeMscRow>();
        var downtimeIndex = 1;
        var deliveryIndex = 0;
        var hoursFrom = 12;
        var hoursTo = 20;
        for (int i = 0; i <= 26; i++) {
            if (i == 0) {
                rows.add(new DowntimeMscRow(ZERO_STRING, TWELVE, ZERO_STRING, ZERO_STRING, ZERO_STRING));
            }
            var downtime = Integer.parseInt(downtimeRate) * downtimeIndex;
            var delivery = Integer.parseInt(deliveryRate) * deliveryIndex;
            rows.add(new DowntimeMscRow(
                String.valueOf(hoursFrom),
                String.valueOf(hoursTo),
                String.valueOf(delivery),
                String.valueOf(downtime),
                String.valueOf(delivery + downtime))
            );
            if (i % 2 == 0) {
                deliveryIndex++;
                hoursFrom += 8;
                hoursTo += 12;
            } else {
                downtimeIndex++;
                hoursFrom += 12;
                hoursTo += 8;
            }
        }
        return rows;
    }

    private List<AdditionalPointFTL> additionalPointFTLS(FTLRequest request, String login) {
        log.info(GENERATE_FTL_ADD_POINT);
        var rows = new ArrayList<AdditionalPointFTL>();
        var ftAddPoint = additionalPointFTL(request, FT_TYPE, COMMODITY_103, TARIFF_CODE_10003, login, 184);
        var cfAddPoint = additionalPointFTL(request, CF_TYPE, COMMODITY_113, TARIFF_CODE_10018, login, 185);
        if (CollectionUtils.isNotEmpty(ftAddPoint.getRows().getRows())) {
            rows.add(ftAddPoint);
        }
        if (CollectionUtils.isNotEmpty(cfAddPoint.getRows().getRows())) {
            rows.add(cfAddPoint);
        }
        return rows;
    }

    private AdditionalPointFTL additionalPointFTL(FTLRequest request, String serviceType, String activity, String tariffCode, String login, int plusDate) {
        return new AdditionalPointFTL(
            TEMPLATE_MOSCOW_ADDITIONAL_POINT,
            String.format(FILENAME_MOSCOW_ADDITIONAL_POINT, serviceType, request.getClientName(), login),
            new AdditionalPointFTLRows(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), plusDate),
                activity,
                tariffCode,
                String.format(DESCRIPTION_ADDITIONAL_POINT, serviceType),
                request.getMoscow()
                    .stream()
                    .filter(e -> requireNonNull(e.getAdditionalPoint()))
                    .map(e -> new AdditionalPointsFTLRow(
                        ZONE_MOSCOW,
                        String.format(TYPE_MAPPER, mapBodyType(e.getBodyType()), mapTruckType(e.getTruckType())),
                        serviceType,
                        e.getAdditionalPoint(),
                        String.valueOf(Integer.parseInt(e.getAdditionalPoint()) * ADDITIONAL_POINT_MULTIPLIER)
                    ))
                    .toList()
            )
        );
    }

    private FTLMoscowRowCapacity ftlMoscowRowCapacity(FTLMoscow row, String zone, String type, String rate) {
        var capacity = row.getCapacity().split("-");
        return new FTLMoscowRowCapacity(
            row.getLoadingLocation(),
            zone,
            mapBodyType(row.getBodyType()),
            capacity[0],
            capacity[1],
            type,
            rate
        );
    }

    private LoadingUnloadingFTL loadingUnloading(FTLRequest request, String login) {
        log.info(GENERATE_FTL_LOAD_UNLOAD);
        return new LoadingUnloadingFTL(
            TEMPLATE_MOSCOW_PRR,
            String.format(FILENAME_MOSCOW_PRR, request.getClientName(), login),
            new LoadingUnloadingFTLRows(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 186),
                COMMODITY_202,
                TARIFF_CODE_10099,
                DESCRIPTION_MOSCOW_PRR,
                request.getMoscow()
                    .stream()
                    .filter(e -> requireNonNull(e.getPrr()))
                    .map(e -> new LoadingUnloadingFTLRow(
                        ZONE_MOSCOW,
                        String.format(TYPE_MAPPER, mapBodyType(e.getBodyType()), mapTruckType(e.getTruckType())),
                        FT_TYPE,
                        e.getPrr()
                    ))
                    .toList()
            )
        );
    }

    private ForwardingFTL forwardingFTL(FTLRequest request, String login) {
        log.info(GENERATE_FTL_FORWARDING);
        return new ForwardingFTL(TEMPLATE_MOSCOW_FORWARDING, String.format(FILENAME_MOSCOW_FORWARDING, request.getClientName(), login),
            new ForwardingFTLRows(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), 187),
                COMMODITY_211,
                TARIFF_CODE_1099,
                DESCRIPTION_MOSCOW_FORWARDING,
                request.getMoscow()
                    .stream()
                    .filter(e -> requireNonNull(e.getForwarding()))
                    .map(e -> new ForwardingFTLRow(
                        ZONE_MOSCOW,
                        String.format(TYPE_MAPPER, mapBodyType(e.getBodyType()), mapTruckType(e.getTruckType())),
                        FT_TYPE,
                        e.getForwarding()
                    ))
                    .toList()
            )
        );
    }
}
