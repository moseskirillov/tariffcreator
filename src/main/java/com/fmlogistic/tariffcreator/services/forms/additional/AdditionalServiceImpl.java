package com.fmlogistic.tariffcreator.services.forms.additional;

import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalModel;
import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalRow;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateAdditional;
import com.fmlogistic.tariffcreator.models.generator.additional.GenerateAdditionalRow;
import com.fmlogistic.tariffcreator.services.generate.GenerateService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitiesService;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitySatelliteService;
import com.fmlogistic.tariffcreator.utils.GenerateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_102;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_103;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.COMMODITY_709;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.FD_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.IZO_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.I_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.LD_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.REF_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.R_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1000;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TARIFF_CODE_1099;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.TENT_TYPE;
import static com.fmlogistic.tariffcreator.utils.GenerateUtils.T_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdditionalServiceImpl implements AdditionalService {

    private static final String FD_DELIVERY_FILENAME = "FD_delivery_%s_%s";
    private static final String FD_DISCOUNT_FILENAME = "FD_discount_%s_%s";
    private static final String LD_DELIVERY_FILENAME = "LD_delivery_%s_%s";
    private static final String LD_PICKUP_FILENAME = "LD_pickup_%s_%s";
    private static final String FD_DELIVERY_DESCRIPTION = "FD доставка TC";
    private static final String FD_DISCOUNT_DESCRIPTION = "FD скидка TC";
    private static final String LD_DELIVERY_DESCRIPTION = "LD доставка TC";
    private static final String LD_PICKUP_DESCRIPTION = "LD забор до TC";
    private static final String FD_DELIVERY_TEMPLATE_NAME = "pooling-additional-delivery";
    private static final String FD_DISCOUNT_TEMPLATE_NAME = "pooling-additional-discount";
    private static final String LD_DELIVERY_TEMPLATE_NAME = "pooling-additional-delivery-ld";
    private static final String LD_PICKUP_TEMPLATE_NAME = "pooling-additional-pickup-ld";

    private static final String TYPE_CARGO_R = "R20";
    private static final String TYPE_CARGO_I = "I20";
    private static final String TYPE_CARGO_T = "T20";

    private final CitiesService citiesService;
    private final GenerateService generateService;
    private final CitySatelliteService citySatelliteService;

    @Override
    public List<String> generate(AdditionalModel model) {
        var files = new ArrayList<String>();
        var login = GenerateUtils.getLogin(model.getEmail());
        var fdDeliveryRows = generateAdditional(model, FD_TYPE, COMMODITY_102, TARIFF_CODE_1099, FD_DELIVERY_DESCRIPTION, 159);
        if (!fdDeliveryRows.getRows().isEmpty()) {
            files.add(generateService.generateTariffFile(
                    FD_DELIVERY_TEMPLATE_NAME, fdDeliveryRows, String.format(FD_DELIVERY_FILENAME, model.getClientName(), login)
            ));
        }
        var fdDiscountRows = generateAdditional(model, FD_TYPE, COMMODITY_709, TARIFF_CODE_1000, FD_DISCOUNT_DESCRIPTION, 160);
        if (!fdDiscountRows.getRows().isEmpty()) {
            fdDiscountRows.getRows().forEach(e -> e.setCustomerDiscount(String.valueOf(Integer.parseInt(e.getCustomerDiscount()) * -1)));
            files.add(generateService.generateTariffFile(
                    FD_DISCOUNT_TEMPLATE_NAME, fdDiscountRows, String.format(FD_DISCOUNT_FILENAME, model.getClientName(), login)
            ));
        }
        var ldDeliveryRows = generateAdditional(model, LD_TYPE, COMMODITY_102, TARIFF_CODE_1000, LD_DELIVERY_DESCRIPTION, 161);
        if (!ldDeliveryRows.getRows().isEmpty()) {
            files.add(generateService.generateTariffFile(
                    LD_DELIVERY_TEMPLATE_NAME, ldDeliveryRows, String.format(LD_DELIVERY_FILENAME, model.getClientName(), login)
            ));
        }
        var ldPickupRows = generateAdditional(model, LD_TYPE, COMMODITY_103, TARIFF_CODE_1099, LD_PICKUP_DESCRIPTION, 162);
        if (!ldPickupRows.getRows().isEmpty()) {
            files.add(generateService.generateTariffFile(
                    LD_PICKUP_TEMPLATE_NAME, ldPickupRows, String.format(LD_PICKUP_FILENAME, model.getClientName(), login)
            ));
        }
        return files;
    }

    private GenerateAdditional generateAdditional(
            AdditionalModel model,
            String serviceType,
            String activity,
            String tariffCode,
            String description,
            int plusDays) {
        return new GenerateAdditional(
                model.getClientName(),
                GenerateUtils.plusDate(model.getDateFrom(), plusDays),
                model.getDateTo(),
                activity,
                serviceType,
                tariffCode,
                description,
                activity.equals(COMMODITY_103)
                        ? generateAdditionalRows(model.getAdditional().stream().findFirst().stream().toList(), serviceType, activity)
                        : generateAdditionalRows(model.getAdditional(), serviceType, activity)
        );
    }

    private List<GenerateAdditionalRow> generateAdditionalRows(List<AdditionalRow> additionalRows, String serviceType, String activity) {
        var resultRows = new ArrayList<GenerateAdditionalRow>();
        var firstList = new ArrayList<GenerateAdditionalRow>();
        var secondList = new ArrayList<GenerateAdditionalRow>();
        var thirdList = new ArrayList<GenerateAdditionalRow>();
        for (var row : additionalRows) {
            if (row.getDeliveryCost() != null && !row.getDeliveryCost().equals(StringUtils.EMPTY)
                    || row.getCustomerDiscount() != null && !row.getCustomerDiscount().equals(StringUtils.EMPTY)
                    || row.getShippingFee() != null && !row.getShippingFee().equals(StringUtils.EMPTY)
                    || row.getFenceFee() != null && !row.getFenceFee().equals(StringUtils.EMPTY)) {
                var city = citiesService.findByName(row.getConsignee());
                var satellites = citySatelliteService.citiesSatellites(row.getConsignee());
                if (activity.equals(COMMODITY_103)) {
                    resultRows.add(generateAdditionalRow(row, StringUtils.SPACE, row.getConsignee(), serviceType, activity));
                    break;
                }
                if (satellites.isEmpty()) {
                    if (city.isException()) {
                        firstList.add(generateAdditionalRow(row, city.getRZone(), row.getConsignee(), serviceType, activity));
                    }
                    if (city.getRZone() == null && city.getCZone() != null) {
                        secondList.add(generateAdditionalRow(row, city.getCZone(), StringUtils.SPACE, serviceType, activity));
                    }
                    if (city.getCZone() != null && city.getRZone() != null) {
                        thirdList.add(generateAdditionalRow(row, city.getCZone(), StringUtils.SPACE, serviceType, activity));
                        thirdList.add(generateAdditionalRow(row, city.getRZone(), StringUtils.SPACE, serviceType, activity));
                    }
                } else {
                    for (var satellite : satellites) {
                        if (satellite.getSatellite() != null && satellite.getRZone() != null) {
                            firstList.add(generateAdditionalRow(row, satellite.getRZone(), satellite.getSatellite(), serviceType, activity));
                        } else if (satellite.getCZone() != null) {
                            secondList.add(generateAdditionalRow(
                                    row,
                                    satellite.getCZone(),
                                    satellite.getSatellite() == null ? StringUtils.SPACE : satellite.getSatellite(),
                                    serviceType,
                                    activity));
                        } else {
                            thirdList.add(generateAdditionalRow(row,
                                    satellite.getRZone(),
                                    satellite.getSatellite() == null ? StringUtils.SPACE : satellite.getSatellite(),
                                    serviceType,
                                    activity));
                        }
                    }
                }
            }
        }
        resultRows.addAll(firstList);
        resultRows.addAll(secondList);
        resultRows.addAll(thirdList);
        return resultRows;
    }

    private GenerateAdditionalRow generateAdditionalRow(
            AdditionalRow row,
            String zone,
            String consignee,
            String serviceType,
            String activity) {
        return new GenerateAdditionalRow(
                row.getLoadingLocation(),
                activity.equals(COMMODITY_103) ? StringUtils.SPACE : zone,
                consignee,
                mapCargoType(row.getCargoType(), activity),
                serviceType,
                row.getDeliveryCost(),
                row.getCustomerDiscount(),
                row.getShippingFee(),
                row.getFenceFee()
        );
    }

    private String mapCargoType(String cargoType, String activity) {
        if (activity.equals(COMMODITY_103)) {
            return switch (cargoType) {
                case REF_TYPE -> R_TYPE;
                case IZO_TYPE -> I_TYPE;
                case TENT_TYPE -> T_TYPE;
                default -> null;
            };
        } else {
            return switch (cargoType) {
                case REF_TYPE -> TYPE_CARGO_R;
                case IZO_TYPE -> TYPE_CARGO_I;
                case TENT_TYPE -> TYPE_CARGO_T;
                default -> null;
            };
        }
    }

}
