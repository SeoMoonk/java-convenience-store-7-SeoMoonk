package product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import product.entity.Product;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;

class ProductServiceTest {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private final PromotionService promotionService = new PromotionService(promotionRepository);
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ProductService productService = new ProductService(promotionService, productRepository);

    @Test
    @DisplayName("파일을 읽어 재고 목록을 불러오고, 저장할 수 있다")
    void t001() {
        //given
        String testFilePath = "src/main/resources/testproducts.md";

        //when
        productService.loadFromFilePath(testFilePath);

        //then
        Product testProduct = productService.getByName("testProduct");
        assertThat(testProduct.getName()).isEqualTo("testProduct");
        assertThat(testProduct.getQuantity()).isEqualTo(3);
        assertThat(testProduct.getPrice()).isEqualTo(1000);
        assertThat(testProduct.getPromotion()).isNull();
    }

    @Test
    @DisplayName("재고를 등록할 때, 프로모션 정보가 있다면 연결시켜준다.")
    void t002() {
        //givne, when
        promotionService.loadFromFilePath("src/main/resources/testpromotion.md");
        productService.loadFromFilePath("src/main/resources/testproducts.md");

        //then
        Product testProduct = productService.getByName("testProduct");
        assertThat(testProduct.getPromotion().getName()).isEqualTo("testPromotion");
    }

}