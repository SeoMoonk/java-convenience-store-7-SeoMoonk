package promotion.constants;

import java.util.ArrayList;
import java.util.List;

public class PromotionStatic {
    public static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";

    public static final String PROMOTION_PRESET_NAME_KEY = "name";
    public static final String PROMOTION_PRESET_CONDITION_QUANTITY_KEY = "buy";
    public static final String PROMOTION_PRESET_BONUS_QUANTITY_KEY = "get";
    public static final String PROMOTION_PRESET_START_DATE_KEY = "start_date";
    public static final String PROMOTION_PRESET_END_DATE_KEY = "end_date";
    public static final int PROMOTION_PRESET_KEY_COUNT = 5;

    public static List<String> getPromotionPresetKeys() {
        List<String> promotionKeys = new ArrayList<>();
        promotionKeys.add(PROMOTION_PRESET_NAME_KEY);
        promotionKeys.add(PROMOTION_PRESET_CONDITION_QUANTITY_KEY);
        promotionKeys.add(PROMOTION_PRESET_BONUS_QUANTITY_KEY);
        promotionKeys.add(PROMOTION_PRESET_START_DATE_KEY);
        promotionKeys.add(PROMOTION_PRESET_END_DATE_KEY);
        return promotionKeys;
    }
}
