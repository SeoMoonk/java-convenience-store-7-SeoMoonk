package product.constants;

import java.util.ArrayList;
import java.util.List;

public enum ProductPresetKeys {
    PRODUCT_NAME_PRESET_KEY("name"),
    PRODUCT_PRICE_PRESET_KEY("price"),
    PRODUCT_QUANTITY_PRESET_KEY("quantity"),
    PRODUCT_PROMOTION_NAME_PRESET_KEY("promotion");

    private String key;

    ProductPresetKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        ProductPresetKeys[] preset = ProductPresetKeys.values();
        for (ProductPresetKeys p : preset) {
            keys.add(p.key);
        }
        return keys;
    }
}
