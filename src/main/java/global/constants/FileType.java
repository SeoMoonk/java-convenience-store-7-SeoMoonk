package global.constants;

import java.util.Collections;
import java.util.List;
import product.constants.ProductPresetKeys;
import promotion.constants.PromotionPresetKeys;

public enum FileType {
    PRODUCT, PROMOTION;

    public List<String> getPresetKeys(FileType fileType) {
        if (fileType.equals(PRODUCT)) {
            return ProductPresetKeys.getKeys();
        }

        if (fileType.equals(PROMOTION)) {
            return PromotionPresetKeys.getKeys();
        }

        return Collections.emptyList();
    }
}
