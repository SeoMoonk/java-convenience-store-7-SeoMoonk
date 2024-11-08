package promotion.contants;

import java.util.ArrayList;
import java.util.List;

public class PromotionStatic {
    public static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";

    public static final String PRESET_NAME_KEY = "name";
    public static final String PRESET_CONDITION_QUANTITY_KEY = "buy";
    public static final String PRESET_BONUS_QUANTITY_KEY = "get";
    public static final String PRESET_START_DATE_KEY = "start_date";
    public static final String PRESET_END_DATE_KEY = "end_date";
    public static final int PRESET_KEY_COUNT = 5;

    public static List<String> getPresetPromotionKeys() {
        List<String> promotionKeys = new ArrayList<>();
        promotionKeys.add(PRESET_NAME_KEY);
        promotionKeys.add(PRESET_CONDITION_QUANTITY_KEY);
        promotionKeys.add(PRESET_BONUS_QUANTITY_KEY);
        promotionKeys.add(PRESET_START_DATE_KEY);
        promotionKeys.add(PRESET_END_DATE_KEY);
        return promotionKeys;
    }
}
