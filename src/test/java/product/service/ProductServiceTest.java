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
    private final ProductService productService = new ProductService(productRepository);

    @Test
    @DisplayName("파일을 읽어 재고 목록을 불러오고, 저장할 수 있다")
    void t001() {
        //given
        String testFilePath = "src/main/resources/testproducts.md";

        //when
        productService.setUp(testFilePath);

        //then
        Product testProduct = productService.getByName("testName");
        assertThat(testProduct.getName()).isEqualTo("testName");
        assertThat(testProduct.getQuantity()).isEqualTo(3);
        assertThat(testProduct.getPrice()).isEqualTo(1000);
        assertThat(testProduct.getPromotionName()).isEqualTo("테스트 프로모션");
    }

    @Test
    @DisplayName("재고를 등록할 때, 프로모션 정보가 있다면 연결시켜준다.")
    void t002() {
        //givne, when
        promotionService.setUp("src/main/resources/testpromotion.md");
        productService.setUp("src/main/resources/testproducts.md");

        //then
        Product testProduct = productService.getByName("testName");
        assertThat(testProduct.getProduct().getName()).isEqualTo("testName");
    }

}