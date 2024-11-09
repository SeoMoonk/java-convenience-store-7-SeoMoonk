package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import product.service.ProductService;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;

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
    @DisplayName("사용자가 구매를 요청했을 때, 물품을 찾을 수 없다면 예외가 발생한다.")
    void t001() {
        //given
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        purchaseRequests.add(new PurchaseRequest("invalid", 3));

        //when, then
        assertThatThrownBy(() -> storeService.tryPurchase(purchaseRequests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    @DisplayName("사용자가 구매를 요청했을 때, 물품의 재고가 부족하다면 예외가 발생한다.")
    void t002() {
        //given
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        purchaseRequests.add(new PurchaseRequest("콜라", 300));

        //when, then
        assertThatThrownBy(() -> storeService.tryPurchase(purchaseRequests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고");
    }

    @Test
    @DisplayName("사용자가 성공적으로 구매를 수행하면, 물품의 보유 수량이 변경된다.")
    void t003() {
        //given
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        purchaseRequests.add(new PurchaseRequest("콜라", 5));
        int beforeQuantity = productService.getByName("콜라").getQuantity();

        //when
        storeService.tryPurchase(purchaseRequests);

        //then
        int afterQuantity = productService.getByName("콜라").getQuantity();
        assertThat(afterQuantity).isLessThan(beforeQuantity);
    }

}