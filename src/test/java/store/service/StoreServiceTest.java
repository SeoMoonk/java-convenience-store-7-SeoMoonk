package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static product.constants.ProductStatic.MONETARY_UNIT;
import static product.constants.ProductStatic.PRODUCT_UNIT;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import product.dto.ProductInfo;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import product.service.ProductService;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;

class StoreServiceTest {

    private static final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private static final PromotionService promotionService = new PromotionService(promotionRepository);
    private static final ProductRepository productRepository = new ProductRepositoryImpl();
    private static final ProductService productService = new ProductService(promotionService, productRepository);
    private static final StoreService storeService = new StoreService(promotionService, productService);

    @BeforeAll
    static void setUp() {
        storeService.setUp();
    }

    @Test
    @DisplayName("상점은 모든 재고에 대한 변환된 정보를 받아올 수 있다")
    void t001() {
        String name = "콜라";
        String price = String.format("%,d", 1000) + MONETARY_UNIT;
        String quantity = 10 + PRODUCT_UNIT;
        String promotionName = "탄산2+1";
        ProductInfo testInfo = new ProductInfo(name, price, quantity, promotionName);

        List<ProductInfo> productInfos = storeService.getProductInfos();

        assertThat(productInfos).contains(testInfo);
    }
}