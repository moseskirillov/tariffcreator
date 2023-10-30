package com.fmlogistic.tariffcreator.utils;

import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class GenerateUtils {

    private static final String DATA_PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATA_PATTERN);
    private static final Calendar CALENDAR = Calendar.getInstance();

    private static final String AT_DELIMITER = "@";

    private static final String FORMULA_90034 = "90034";
    private static final String FORMULA_1000 = "1000";

    public static final String MULTIPLIER_RETURN = "1.5";
    public static final int ZERO = 0;

    public static final String MARGIN_TYPE = "Margin";

    public static final String MOSCOW_FIRST_ZONE = "Москва 1 зона";
    public static final String MOSCOW_SECOND_ZONE = "Москва 2 зона";
    public static final String MOSCOW_THIRD_ZONE = "Москва 3 зона";
    public static final String MOSCOW_FOURTH_ZONE = "Москва 4 зона";

    public static final String ZONE_1 = "1";
    public static final String ZONE_2 = "2";
    public static final String ZONE_3 = "3";
    public static final String ZONE_4 = "4";
    public static final String ZONE_MOSCOW = "MOSCOW";

    public static final List<String> FOOD_TYPES = List.of("Food", "Non-food");
    public static final List<String> ALCO_TYPE = List.of("Alco");

    public static final String LOADING_UNIT_EP = "EP";
    public static final String LOADING_UNIT_LP = "LP";
    public static final String LOADING_UNIT_HP = "HP";
    public static final String LOADING_UNIT_OT = "OT";

    public static final String LTL_TYPE = "LTL";

    public static final String LTL_TARIFF_TYPE = "ltl";
    public static final String POOLING_TARIFF_TYPE = "pooling";

    public static final String LT_TYPE = "LT";
    public static final String MG_TYPE = "MG";
    public static final String PF_TYPE = "PF";
    public static final String PL_TYPE = "PL";
    public static final String LD_TYPE = "LD";
    public static final String FT_TYPE = "FT";
    public static final String CF_TYPE = "CF";
    public static final String FD_TYPE = "FD";
    public static final String LZ_TYPE = "LZ";
    public static final String ZL_TYPE = "ZL";
    public static final String PL_LT_RETAIL = "PL = LT retail";

    public static final String POOLING_FORM_NAME = "Pooling";
    public static final String FTL_FORM_NAME = "FTL";

    public static final String COMMODITY_102 = "102";
    public static final String COMMODITY_103 = "103";
    public static final String COMMODITY_104 = "104";
    public static final String COMMODITY_107 = "107";
    public static final String COMMODITY_109 = "109";
    public static final String COMMODITY_113 = "113";
    public static final String COMMODITY_201 = "201";
    public static final String COMMODITY_202 = "202";
    public static final String COMMODITY_207 = "207";
    public static final String COMMODITY_210 = "210";
    public static final String COMMODITY_211 = "211";
    public static final String COMMODITY_402 = "402";
    public static final String COMMODITY_500 = "500";
    public static final String COMMODITY_709 = "709";

    public static final String T_TYPE = "T";
    public static final String R_TYPE = "R";
    public static final String I_TYPE = "I";

    public static final String REF_TYPE = "реф";
    public static final String IZO_TYPE = "изо";
    public static final String TENT_TYPE = "тент";

    public static final String CONSIGNEE_TYPE_RETAIL = "сети";
    public static final String CONSIGNEE_TYPE_OTHER = "другое";
    public static final String CONSIGNEE_TYPE_RETAIL_EN = "retail";

    public static final String RETURN_SIGN = "RE";

    public static final String ZERO_STRING = "0";

    public static final String TARIFF_CODE_1000 = "1000";
    public static final String TARIFF_CODE_1099 = "1099";
    public static final String TARIFF_CODE_1399 = "1399";
    public static final String TARIFF_CODE_10003 = "10003";
    public static final String TARIFF_CODE_10018 = "10018";
    public static final String TARIFF_CODE_10099 = "10099";

    public static final String TYPE_MAPPER = "%s%s";

    public static final String MOSCOW_LOADING_LOCATION = "Moscow";
    public static final String REGION_LOADING_LOCATION = "Region";

    public static final String PLM = "PLM_";

    public static final Map<String, String> MOSCOW_ZONES_MAP = Map.of(
            MOSCOW_FIRST_ZONE, "1",
            MOSCOW_SECOND_ZONE, "2",
            MOSCOW_THIRD_ZONE, "3",
            MOSCOW_FOURTH_ZONE, "4"
    );

    public static String mapFileName(String filename, String type, String clientName, String email) {
        return String.format(filename, type, clientName, email);
    }

    public static String mapTruckType(String truckType) {
        return truckType.substring(0, truckType.length() - 1);
    }

    public static boolean requireNonNull(String row) {
        return row != null && !row.isEmpty() && !row.equals(ZERO_STRING);
    }

    public static String mapBodyType(String bodyType) {
        switch (bodyType) {
            case TENT_TYPE -> {
                return T_TYPE;
            }
            case IZO_TYPE -> {
                return I_TYPE;
            }
            case REF_TYPE -> {
                return R_TYPE;
            }
            default -> {
                return bodyType;
            }
        }
    }

    public static String margin(String row) {
        var cost = new BigDecimal(row);
        cost = cost.multiply(new BigDecimal(MULTIPLIER_RETURN));
        cost = cost.setScale(ZERO, RoundingMode.DOWN);
        return cost.toString();
    }

    public static String tariffFormula(String commodityCode, boolean isReturn) {
        if (isReturn) {
            return FORMULA_1000;
        }
        for (var code : FOOD_TYPES) {
            if (commodityCode.equals(code)) {
                return FORMULA_1000;
            }
        }
        return FORMULA_90034;
    }

    public static String finString(String commodityCode, boolean isReturn) {
        if (isReturn) {
            return COMMODITY_402;
        }
        for (var code : FOOD_TYPES) {
            if (commodityCode.equals(code)) {
                return COMMODITY_102;
            }
        }
        return COMMODITY_109;
    }

    public static String getLogin(String email) {
        return email.split(AT_DELIMITER)[0];
    }

    @SneakyThrows
    public static String plusDate(String dateString, int days) {
        CALENDAR.setTime(FORMATTER.parse(dateString));
        CALENDAR.add(Calendar.DATE, days);
        return FORMATTER.format(CALENDAR.getTime());
    }
}
