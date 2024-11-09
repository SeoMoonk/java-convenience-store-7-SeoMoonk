package promotion.constants;

import java.util.ArrayList;
import java.util.List;
import product.constants.ProductPresetKeys;

public enum PromotionPresetKeys {

    PROMOTION_NAME_PRESET_KEY("name"),
    PROMOTION_CONDITION_QUANTITY_PRESET_KEY("buy"),
    PROMOTION_BONUS_QUANTITY_PRESET_KEY("get"),
    PROMOTION_START_DATE_PRESET_KEY("start_date"),
    PROMOTION_END_DATE_PRESET_KEY("end_date");

    private String key;

    PromotionPresetKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        PromotionPresetKeys[] preset = PromotionPresetKeys.values();
        for (PromotionPresetKeys p : preset) {
            keys.add(p.key);
        }
        return keys;
    }
}
