package product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static store.constants.StoreErrorCode.NONE_EXISTENT_PRODUCT;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import product.dto.ProductInfo;
import product.entity.Product;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import promotion.entity.Promotion;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;
import store.dto.request.PurchaseForm;
import store.dto.request.PurchaseRequest;

class ProductServiceTest {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private final PromotionService promotionService = new PromotionService(promotionRepository);
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ProductService productService = new ProductService(promotionService, productRepository);

    @BeforeEach
    void setUp() {
        promotionService.loadFromFilePath("src/main/resources/testpromotion.md");
        productService.loadFromFilePath("src/main/resources/testproducts.md");
    }

    @Test
    @DisplayName("파일을 읽어 재고 목록을 불러오고, 저장할 수 있다")
    void t001() {
        Product testProduct = productService.getByName("testProduct");
        assertThat(testProduct.getName()).isEqualTo("testProduct");
        assertThat(testProduct.getQuantity()).isEqualTo(3);
        assertThat(testProduct.getPrice()).isEqualTo(1000);
        assertThat(testProduct.getPromotion().getName()).isEqualTo("testPromotion");
    }

    @Test
    @DisplayName("재고를 등록할 때, 프로모션 정보가 있다면 연결시켜준다.")
    void t002() {
        Product testProduct = productService.getByName("testProduct");
        assertThat(testProduct.getPromotion().getName()).isEqualTo("testPromotion");
    }

    @Test
    @DisplayName("재고를 등록할 때, 프로모션 재고만 존재한다면 일반 재고를 함께 등록시킨다")
    void t003() {
        assertThat(productService.getAllByName("testProduct").size()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"구매하고싶은상품", "없는상품", "다팔린상품"})
    @DisplayName("이름이 일치하는 상품이 없다면, 예외를 발생시킨다")
    void t004(String name) {
        assertThatThrownBy(() -> productService.getByName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(NONE_EXISTENT_PRODUCT.getMsgWithPrefix());
    }

    @Test
    @DisplayName("이름이 일치하는 상품을 가져올 수 있다")
    void t005() {
        String testName = "testProduct";

        Product productByName = productService.getByName(testName);

        assertThat(productByName.getName()).isEqualTo(testName);
    }

    @Test
    @DisplayName("상품의 정보를 포맷팅하여 특정 폼에 담아 가져올 수 있다")
    void t006() {
        ProductInfo productInfo = new ProductInfo("testProduct", "1,000원", "3개", "testPromotion");

        List<ProductInfo> productInfos = productService.getProductInfos();

        assertThat(productInfos.contains(productInfo)).isTrue();
    }

    @Test
    @DisplayName("이름을 가지고 프로모션 재고와 일반 재고의 총 합을 가져올 수 있다")
    void t007() {
        int storedTotalQuantity = productService.getAllQuantityByName("testProduct");

        assertThat(storedTotalQuantity).isEqualTo(13);
    }

    @Test
    @DisplayName("프로모션이 적용되는 상품인지 아닌지 판별할 수 있다")
    void t008() {
        Product product = productService.getByName("testProduct");
        List<Promotion> activePromotions = promotionService.getActivePromotions();
        PurchaseRequest purchaseRequest = new PurchaseRequest(product.getName(), 3);

        assertThat(productService.isPromotionTargetRequest(activePromotions, purchaseRequest)).isTrue();
    }

    @Test
    @DisplayName("이름이 주어지면, 프로모션이 있는 제품과 없는 제품을 선택하여 가져올 수 있다")
    void t009() {
        Promotion testPromotion = promotionService.getByName("testPromotion");
        Product promotionProduct = new Product("testProduct", 1000, 3, testPromotion);
        Product normalProduct = new Product("testProduct", 1000, 10, null);
        Product containsPromotion = productService.getByNameAndHasPromotion("testProduct");
        Product nonePromotion = productService.getByNameAndNotHasPromotion("testProduct");

        assertThat(containsPromotion.getQuantity()).isEqualTo(promotionProduct.getQuantity());
        assertThat(nonePromotion.getQuantity()).isEqualTo(normalProduct.getQuantity());
    }

    @Test
    @DisplayName("결제를 수행하면, 수량이 차감된다")
    void t010() {
        List<PurchaseForm> purchaseForms = new ArrayList<>();
        Product beforeProduct = productService.getByNameAndNotHasPromotion("testProduct");
        purchaseForms.add(new PurchaseForm(beforeProduct, 2));
        int beforeQuantity = beforeProduct.getQuantity();

        productService.purchase(purchaseForms);

        Product afterProduct = productService.getByNameAndNotHasPromotion("testProduct");
        assertThat(afterProduct.getQuantity()).isEqualTo(beforeQuantity - 2);
    }

}