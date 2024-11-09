package store.service;

import static product.constants.ProductStatic.PRODUCT_FILE_PATH;
import static promotion.constants.PromotionStatic.PROMOTION_FILE_PATH;

import java.util.List;
import product.dto.response.ProductInfo;
import product.service.ProductService;
import promotion.service.PromotionService;

public class StoreService {

    private final PromotionService promotionService;
    private final ProductService productService;

    public StoreService(PromotionService promotionService, ProductService productService) {
        this.promotionService = promotionService;
        this.productService = productService;
    }

    public void setUp() {
        promotionService.loadFromFilePath(PROMOTION_FILE_PATH);
        productService.loadFromFilePath(PRODUCT_FILE_PATH);
    }

    public List<ProductInfo> getProductInfos() {
        return productService.getProductInfos();
    }
}
