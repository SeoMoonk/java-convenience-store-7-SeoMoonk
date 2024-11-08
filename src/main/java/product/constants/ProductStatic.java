package product.constants;

import java.util.ArrayList;
import java.util.List;

public class ProductStatic {
    public static final String PRODUCT_FILE_PATH = "src/main/resources/products.md";

    public static final String PRODUCT_PRESET_NAME_KEY = "name";
    public static final String PRODUCT_PRESET_PRICE_KEY = "price";
    public static final String PRODUCT_PRESET_QUANTITY_KEY = "quantity";
    public static final String PRODUCT_PRESET_PROMOTION_NAME_KEY = "promotion";
    public static final int PRODUCT_PRESET_KEY_COUNT = 4;

    public static List<String> getProductPresetKeys() {
        List<String> productPresetKeys = new ArrayList<>();
        productPresetKeys.add(PRODUCT_PRESET_NAME_KEY);
        productPresetKeys.add(PRODUCT_PRESET_PRICE_KEY);
        productPresetKeys.add(PRODUCT_PRESET_QUANTITY_KEY);
        productPresetKeys.add(PRODUCT_PRESET_PROMOTION_NAME_KEY);
        return productPresetKeys;
    }
}
