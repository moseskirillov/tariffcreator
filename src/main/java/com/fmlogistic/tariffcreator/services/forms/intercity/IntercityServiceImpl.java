package com.fmlogistic.tariffcreator.services.forms.intercity;

import com.fmlogistic.tariffcreator.models.generator.additional.FTLIntercityModel;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercity;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercityMscRegion;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercityMscRegionRow;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercityRegionMsc;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercityRegionMscRow;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercityRegionRegion;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateFTLIntercityRegionRegionRow;
import com.fmlogistic.tariffcreator.models.generator.request.FTLRequest;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitiesService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitySatelliteService;
import com.fmlogistic.tariffcreator.utils.GenerateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.CF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_102;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_107;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.FT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.I_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.R_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1099;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1399;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TYPE_MAPPER;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.T_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.ZONE_MOSCOW;

@Service
@RequiredArgsConstructor
public class IntercityServiceImpl implements IntercityService {

    private static final String MOSCOW_DIGITAL_NAME = "зона";

    private static final String MSC_REG_TEMPLATE_NAME = "ftl-intercity-msc-reg";
    private static final String MSC_REG_FILE_NAME = "%s_intercity_load_MSC_%s_%s";
    private static final String MSC_REG_DESCRIPTION = "%s зона загрузки Мск ТТ";
    private static final String REG_MCS_TEMPLATE_NAME = "ftl-intercity-reg-msc";
    private static final String REG_MSC_FILE_NAME = "%s_intercity_REGION_%s_%s";
    private static final String REG_MSC_DESCRIPTION = "%s зона выгрузки Мск TT";
    private static final String REG_REG_TEMPLATE_NAME = "ftl-intercity-reg-reg";
    private static final String REG_REG_FILE_NAME = "%s_intercity_unload_MSC_%s_%s";
    private static final String REG_REG_DESCRIPTION = "%s межгород регионы TT";

    private final CitiesService citiesService;
    private final GenerateService generateService;
    private final CitySatelliteService citySatelliteService;

    @Override
    public List<String> generate(FTLRequest request) {
        var files = new ArrayList<String>();
        var result = generateFTLIntercity(request);
        var login = GenerateUtils.getLogin(request.getEmail());
        for (var rows : result.getMscRegionRows()) {
            if (CollectionUtils.isNotEmpty(rows.getMscRegRows())) {
                files.add(generateService.generateTariffFile(MSC_REG_TEMPLATE_NAME, rows, String.format(MSC_REG_FILE_NAME, rows.getDeliveryType(), request.getClientName(), login)));
            }
        }
        for (var rows : result.getRegionMscRows()) {
            if (CollectionUtils.isNotEmpty(rows.getRegMSCRows())) {
                files.add(generateService.generateTariffFile(REG_MCS_TEMPLATE_NAME, rows, String.format(REG_MSC_FILE_NAME, rows.getDeliveryType(), request.getClientName(), login)));
            }
        }
        for (var rows : result.getRegionRegionRows()) {
            if (CollectionUtils.isNotEmpty(rows.getRegRegRows())) {
                files.add(generateService.generateTariffFile(REG_REG_TEMPLATE_NAME, rows, String.format(REG_REG_FILE_NAME, rows.getDeliveryType(), request.getClientName(), login)));
            }
        }
        return files;
    }

    private GenerateFTLIntercity generateFTLIntercity(FTLRequest request) {
        return new GenerateFTLIntercity(
                List.of(generateFTLIntercityMscRegion(request, COMMODITY_102, 163), generateFTLIntercityMscRegion(request, COMMODITY_107, 164)),
                List.of(generateFTLIntercityRegionMsc(request, COMMODITY_102, 165), generateFTLIntercityRegionMsc(request, COMMODITY_107, 166)),
                List.of(generateFTLIntercityRegionRegion(request, COMMODITY_102, 167), generateFTLIntercityRegionRegion(request, COMMODITY_107, 168))
        );
    }

    private GenerateFTLIntercityMscRegion generateFTLIntercityMscRegion(FTLRequest request, String activity, int plusDate) {
        var serviceType = activity.equals(COMMODITY_102) ? FT_TYPE : CF_TYPE;
        return new GenerateFTLIntercityMscRegion(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), plusDate),
                activity,
                serviceType,
                activity.equals(COMMODITY_102) ? TARIFF_CODE_1099 : TARIFF_CODE_1399,
                String.format(MSC_REG_DESCRIPTION, serviceType),
                generateFTLIntercityMscRegionRows(filterLoadMsc(request.getIntercity()), serviceType)
        );
    }

    private GenerateFTLIntercityRegionMsc generateFTLIntercityRegionMsc(FTLRequest request, String activity, int plusDate) {
        var serviceType = activity.equals(COMMODITY_102) ? FT_TYPE : CF_TYPE;
        return new GenerateFTLIntercityRegionMsc(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), plusDate),
                activity,
                serviceType,
                activity.equals(COMMODITY_102) ? TARIFF_CODE_1099 : TARIFF_CODE_1399,
                String.format(REG_MSC_DESCRIPTION, serviceType),
                generateFTLIntercityRegionMscRows(filterUnloadRegion(request.getIntercity()), serviceType)
        );
    }

    private GenerateFTLIntercityRegionRegion generateFTLIntercityRegionRegion(FTLRequest request, String activity, int plusDate) {
        var serviceType = activity.equals(COMMODITY_102) ? FT_TYPE : CF_TYPE;
        return new GenerateFTLIntercityRegionRegion(
                request.getClientName(),
                request.getDateFrom(),
                GenerateUtils.plusDate(request.getDateTo(), plusDate),
                activity,
                serviceType,
                activity.equals(COMMODITY_102) ? TARIFF_CODE_1099 : TARIFF_CODE_1399,
                String.format(REG_REG_DESCRIPTION, serviceType),
                generateFTLIntercityRegionRegionRows(filterLoadRegion(request.getIntercity()), serviceType)
        );
    }

    private List<GenerateFTLIntercityMscRegionRow> generateFTLIntercityMscRegionRows(List<FTLIntercityModel> intercity, String serviceType) {
        var rows = new ArrayList<GenerateFTLIntercityMscRegionRow>();
        var firstList = new ArrayList<GenerateFTLIntercityMscRegionRow>();
        var secondList = new ArrayList<GenerateFTLIntercityMscRegionRow>();
        var thirdList = new ArrayList<GenerateFTLIntercityMscRegionRow>();
        for (var row : intercity) {
            var city = citiesService.findByName(row.getDestination());
            var satellites = citySatelliteService.citiesSatellites(row.getDestination());
            if (satellites.isEmpty()) {
                if (city != null) {
                    if (city.isException()) {
                        firstList.addAll(generateFTLIntercityMscRegionRows(row, city.getRZone(), row.getDestination(), serviceType));
                    }
                    if (city.getRZone() == null && city.getCZone() != null) {
                        secondList.addAll(generateFTLIntercityMscRegionRows(row, city.getCZone(), StringUtils.SPACE, serviceType));
                    }
                    if (city.getCZone() != null && city.getRZone() != null) {
                        thirdList.addAll(generateFTLIntercityMscRegionRows(row, city.getCZone(), StringUtils.SPACE, serviceType));
                        thirdList.addAll(generateFTLIntercityMscRegionRows(row, city.getRZone(), StringUtils.SPACE, serviceType));
                    }
                }
            } else {
                for (var satellite : satellites) {
                    if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                        firstList.addAll(generateFTLIntercityMscRegionRows(row, satellite.getRZone(), satellite.getSatellite(), serviceType));
                    } else if (satellite.getCZone() != null) {
                        secondList.addAll(generateFTLIntercityMscRegionRows(row, satellite.getCZone(), satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), serviceType));
                    } else {
                        thirdList.addAll(generateFTLIntercityMscRegionRows(row, satellite.getRZone(), satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), serviceType));
                    }
                }
            }
        }
        rows.addAll(firstList);
        rows.addAll(secondList);
        rows.addAll(thirdList);
        return rows;
    }

    private List<GenerateFTLIntercityRegionMscRow> enrichRowsDeparture(
            FTLIntercityModel row,
            String destinationZone,
            String destinationCity,
            String serviceType,
            int queue
    ) {
        var cityDeparture = citiesService.findByName(row.getDeparture());
        var satellitesDeparture = citySatelliteService.citiesSatellites(row.getDeparture());
        if (satellitesDeparture.isEmpty()) {
            if (cityDeparture != null) {
                if (cityDeparture.isException()) {
                    return new ArrayList<>(generateFTLIntercityRegionMscRows(
                            row,
                            cityDeparture.getRZone(),
                            row.getDeparture(),
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            queue == 1 ? 1 : queue == 2 ? 4 : 5
                    ));
                }
                if (cityDeparture.getCZone() != null && cityDeparture.getRZone() == null) {
                    return new ArrayList<>(generateFTLIntercityRegionMscRows(
                            row,
                            cityDeparture.getCZone(),
                            StringUtils.SPACE,
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            queue == 1 ? 2 : queue == 2 ? 6 : 7
                    ));
                }
                if (cityDeparture.getRZone() != null && cityDeparture.getCZone() != null) {
                    var resultRows = new ArrayList<GenerateFTLIntercityRegionMscRow>();
                    resultRows.addAll(generateFTLIntercityRegionMscRows(
                            row,
                            cityDeparture.getRZone(),
                            StringUtils.SPACE,
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            queue == 1 ? 3 : queue == 2 ? 8 : 9
                    ));
                    resultRows.addAll(generateFTLIntercityRegionMscRows(
                            row,
                            cityDeparture.getCZone(),
                            StringUtils.SPACE,
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            queue == 1 ? 3 : queue == 2 ? 8 : 9
                    ));
                    return resultRows;
                }
            }
        } else {
            for (var satellite : satellitesDeparture) {
                if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                    return new ArrayList<>(generateFTLIntercityRegionMscRows(
                            row,
                            satellite.getRZone(),
                            satellite.getSatellite(),
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            queue == 1 ? 1 : queue == 2 ? 4 : 5
                    ));
                } else if (satellite.getCZone() != null) {
                    return new ArrayList<>(generateFTLIntercityRegionMscRows(
                            row,
                            satellite.getCZone(),
                            satellite.getSatellite(),
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            queue == 1 ? 2 : queue == 2 ? 6 : 7
                    ));
                } else {
                    return new ArrayList<>(generateFTLIntercityRegionMscRows(
                            row,
                            satellite.getRZone(),
                            StringUtils.SPACE,
                            destinationZone,
                            destinationCity,
                            serviceType,
                            queue,
                            0
                    ));
                }
            }
        }
        return List.of();
    }

    private List<GenerateFTLIntercityRegionMscRow> generateFTLIntercityRegionMscRows(List<FTLIntercityModel> intercity, String serviceType) {
        var rows = new ArrayList<GenerateFTLIntercityRegionMscRow>();
        for (var row : intercity) {
            var cityDestination = citiesService.findByName(row.getDestination());
            var satellitesDestination = citySatelliteService.citiesSatellites(row.getDestination());
            if (satellitesDestination.isEmpty()) {
                if (cityDestination != null) {
                    if (cityDestination.isException()) {
                        rows.addAll(enrichRowsDeparture(
                                row,
                                cityDestination.getRZone(),
                                row.getDestination(),
                                serviceType,
                                1
                        ));
                    }
                    if (cityDestination.getRZone() == null && cityDestination.getCZone() != null) {
                        rows.addAll(enrichRowsDeparture(
                                row,
                                cityDestination.getCZone(),
                                StringUtils.SPACE,
                                serviceType,
                                2
                        ));
                    }
                    if (cityDestination.getRZone() != null && cityDestination.getCZone() != null) {
                        rows.addAll(enrichRowsDeparture(
                                row,
                                cityDestination.getRZone(),
                                StringUtils.SPACE,
                                serviceType,
                                3
                        ));
                        rows.addAll(enrichRowsDeparture(
                                row,
                                cityDestination.getCZone(),
                                StringUtils.SPACE,
                                serviceType,
                                3
                        ));
                    }
                }
            } else {
                for (var satellite : satellitesDestination) {
                    if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                        rows.addAll(enrichRowsDeparture(
                                row,
                                satellite.getRZone(),
                                satellite.getSatellite(),
                                serviceType,
                                1
                        ));
                    } else if (satellite.getCZone() != null) {
                        rows.addAll(enrichRowsDeparture(
                                row,
                                satellite.getCZone(),
                                satellite.getSatellite() == null ? StringUtils.SPACE : satellite.getSatellite(),
                                serviceType,
                                2
                        ));
                    } else {
                        rows.addAll(enrichRowsDeparture(
                                row,
                                satellite.getRZone(),
                                StringUtils.SPACE,
                                serviceType,
                                3
                        ));
                    }
                }
            }
        }
        return rows
                .stream()
                .sorted(Comparator.comparing(GenerateFTLIntercityRegionMscRow::getFinalQueue))
                .toList();
    }

    private List<GenerateFTLIntercityRegionRegionRow> generateFTLIntercityRegionRegionRows(List<FTLIntercityModel> intercity, String serviceType) {
        var rows = new ArrayList<GenerateFTLIntercityRegionRegionRow>();
        var firstList = new ArrayList<GenerateFTLIntercityRegionRegionRow>();
        var secondList = new ArrayList<GenerateFTLIntercityRegionRegionRow>();
        var thirdList = new ArrayList<GenerateFTLIntercityRegionRegionRow>();
        for (var row : intercity) {
            var city = citiesService.findByName(row.getDeparture());
            var satellites = citySatelliteService.citiesSatellites(row.getDeparture());
            if (satellites.isEmpty()) {
                if (city != null) {
                    if (city.isException()) {
                        firstList.addAll(generateFTLIntercityRegionRegionRows(row, city.getRZone(), row.getDeparture(), serviceType));
                    }
                    if (city.getRZone() == null && city.getCZone() != null) {
                        secondList.addAll(generateFTLIntercityRegionRegionRows(row, city.getCZone(), StringUtils.SPACE, serviceType));
                    }
                    if (city.getCZone() != null && city.getRZone() != null) {
                        thirdList.addAll(generateFTLIntercityRegionRegionRows(row, city.getCZone(), StringUtils.SPACE, serviceType));
                        thirdList.addAll(generateFTLIntercityRegionRegionRows(row, city.getRZone(), StringUtils.SPACE, serviceType));
                    }
                }
            } else {
                for (var satellite : satellites) {
                    if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                        firstList.addAll(generateFTLIntercityRegionRegionRows(row, satellite.getRZone(), satellite.getSatellite(), serviceType));
                    } else if (satellite.getCZone() != null) {
                        secondList.addAll(generateFTLIntercityRegionRegionRows(row, satellite.getCZone(), satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), serviceType));
                    } else {
                        thirdList.addAll(generateFTLIntercityRegionRegionRows(row, satellite.getRZone(), satellite.getSatellite() == null
                                ? StringUtils.SPACE
                                : satellite.getSatellite(), serviceType));
                    }
                }
            }
        }
        rows.addAll(firstList);
        rows.addAll(secondList);
        rows.addAll(thirdList);
        return rows;
    }

    private List<GenerateFTLIntercityMscRegionRow> generateFTLIntercityMscRegionRows(FTLIntercityModel row, String unloadingZone, String unloadingCity, String serviceType) {
        var rows = new ArrayList<GenerateFTLIntercityMscRegionRow>();
        if (StringUtils.isNotEmpty(row.getTent())) {
            rows.add(generateFTLIntercityMscRegionRow(row, unloadingZone, unloadingCity, row.getTent(), serviceType, T_TYPE));
        }
        if (StringUtils.isNotEmpty(row.getIzo())) {
            rows.add(generateFTLIntercityMscRegionRow(row, unloadingZone, unloadingCity, row.getIzo(), serviceType, I_TYPE));
        }
        if (StringUtils.isNotEmpty(row.getRef())) {
            rows.add(generateFTLIntercityMscRegionRow(row, unloadingZone, unloadingCity, row.getRef(), serviceType, R_TYPE));
        }
        return rows;
    }

    private List<GenerateFTLIntercityRegionMscRow> generateFTLIntercityRegionMscRows(
            FTLIntercityModel row,
            String loadingZone,
            String loadingCity,
            String unloadingZone,
            String unloadingCity,
            String serviceType,
            int queue,
            int finalQueue
    ) {
        var rows = new ArrayList<GenerateFTLIntercityRegionMscRow>();
        if (StringUtils.isNotEmpty(row.getTent())) {
            rows.add(generateFTLIntercityRegionMscRow(row, loadingZone, loadingCity, unloadingZone, unloadingCity, row.getTent(), serviceType, T_TYPE, queue, finalQueue));
        }
        if (StringUtils.isNotEmpty(row.getIzo())) {
            rows.add(generateFTLIntercityRegionMscRow(row, loadingZone, loadingCity, unloadingZone, unloadingCity, row.getIzo(), serviceType, I_TYPE, queue, finalQueue));
        }
        if (StringUtils.isNotEmpty(row.getRef())) {
            rows.add(generateFTLIntercityRegionMscRow(row, loadingZone, loadingCity, unloadingZone, unloadingCity, row.getRef(), serviceType, R_TYPE, queue, finalQueue));
        }
        return rows;
    }

    private List<GenerateFTLIntercityRegionRegionRow> generateFTLIntercityRegionRegionRows(FTLIntercityModel row, String loadingZone, String loadingCity, String serviceType) {
        var rows = new ArrayList<GenerateFTLIntercityRegionRegionRow>();
        if (StringUtils.isNotEmpty(row.getTent())) {
            rows.add(generateFTLIntercityRegionRegionRow(row, loadingZone, loadingCity, row.getDestination(), serviceType, T_TYPE, row.getTent()));
        }
        if (StringUtils.isNotEmpty(row.getIzo())) {
            rows.add(generateFTLIntercityRegionRegionRow(row, loadingZone, loadingCity, row.getDestination(), serviceType, I_TYPE, row.getIzo()));
        }
        if (StringUtils.isNotEmpty(row.getRef())) {
            rows.add(generateFTLIntercityRegionRegionRow(row, loadingZone, loadingCity, row.getDestination(), serviceType, R_TYPE, row.getRef()));
        }
        return rows;
    }

    private GenerateFTLIntercityMscRegionRow generateFTLIntercityMscRegionRow(
            FTLIntercityModel row,
            String unloadingZone,
            String unloadingCity,
            String rate,
            String serviceType,
            String bodyType
    ) {
        return new GenerateFTLIntercityMscRegionRow(
                ZONE_MOSCOW,
                StringUtils.getDigits(row.getDeparture()),
                unloadingZone,
                unloadingCity,
                String.format(TYPE_MAPPER, bodyType, row.getTransportType().substring(0, row.getTransportType().length() - 1)),
                serviceType,
                rate,
                rate,
                rate
        );
    }

    private GenerateFTLIntercityRegionMscRow generateFTLIntercityRegionMscRow(
            FTLIntercityModel row,
            String loadingZone,
            String loadingCity,
            String unloadingZone,
            String unloadingCity,
            String rate,
            String serviceType,
            String bodyType,
            int queue,
            int finalQueue
    ) {
        return new GenerateFTLIntercityRegionMscRow(
                loadingZone,
                loadingCity,
                unloadingZone,
                unloadingCity,
                String.format(TYPE_MAPPER, bodyType, row.getTransportType().substring(0, row.getTransportType().length() - 1)),
                serviceType,
                rate,
                rate,
                rate,
                queue,
                finalQueue
        );
    }

    private GenerateFTLIntercityRegionRegionRow generateFTLIntercityRegionRegionRow(
            FTLIntercityModel row,
            String loadingZone,
            String loadingCity,
            String unloadingZone,
            String serviceType,
            String bodyType,
            String rate
    ) {
        return new GenerateFTLIntercityRegionRegionRow(
                loadingZone,
                loadingCity,
                ZONE_MOSCOW,
                StringUtils.getDigits(unloadingZone),
                String.format(TYPE_MAPPER, bodyType, row.getTransportType().substring(0, row.getTransportType().length() - 1)),
                serviceType,
                rate,
                rate,
                rate
        );
    }

    private List<FTLIntercityModel> filterLoadMsc(List<FTLIntercityModel> rows) {
        return rows.stream()
                .filter(e -> StringUtils.isNotEmpty(e.getDeparture()) && e.getDeparture().contains(MOSCOW_DIGITAL_NAME))
                .filter(e -> StringUtils.isNotEmpty(e.getDestination()) && !e.getDestination().contains(MOSCOW_DIGITAL_NAME))
                .toList();
    }

    private List<FTLIntercityModel> filterUnloadRegion(List<FTLIntercityModel> rows) {
        return rows.stream()
                .filter(e -> StringUtils.isNotEmpty(e.getDeparture()) && !e.getDeparture().contains(MOSCOW_DIGITAL_NAME))
                .filter(e -> StringUtils.isNotEmpty(e.getDestination()) && !e.getDestination().contains(MOSCOW_DIGITAL_NAME))
                .toList();
    }

    private List<FTLIntercityModel> filterLoadRegion(List<FTLIntercityModel> rows) {
        return rows.stream()
                .filter(e -> StringUtils.isNotEmpty(e.getDeparture()) && !e.getDeparture().contains(MOSCOW_DIGITAL_NAME))
                .filter(e -> StringUtils.isNotEmpty(e.getDestination()) && e.getDestination().contains(MOSCOW_DIGITAL_NAME))
                .toList();
    }

}