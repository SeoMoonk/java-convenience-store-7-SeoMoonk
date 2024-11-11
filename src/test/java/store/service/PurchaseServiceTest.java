package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import product.service.ProductService;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;

class PurchaseServiceTest {

    private static final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private static final PromotionService promotionService = new PromotionService(promotionRepository);
    private static final ProductRepository productRepository = new ProductRepositoryImpl();
    private static final ProductService productService = new ProductService(promotionService, productRepository);
    private static final StoreService storeService = new StoreService(promotionService, productService);
    private static final PurchaseService purchaseService = new PurchaseService(promotionService, productService);

    @BeforeAll
    static void setUp() {
        storeService.setUp();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 6, 9})
    @DisplayName("정확히 프로모션 적용이 가능한 내용의 state는 FULL_PROMOTION_APPLIED 다")
    void t001(int requiredQuantity) {
        //given
        PurchaseRequest request = new PurchaseRequest("콜라", requiredQuantity);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        //when
        PromotionApplyResult applyResult = purchaseService.promotionApplyRequest(purchaseRequests).get(0);

        //then
        assertThat(applyResult.state()).isEqualTo(PromotionApplyState.FULL_PROMOTION_APPLIED);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 8})
    @DisplayName("추가 증정이 가능한 요청의 state는 ADDITIONAL_PROMOTION_AVAILABLE 다")
    void t002(int requiredQuantity) {
        //given
        PurchaseRequest request = new PurchaseRequest("콜라", requiredQuantity);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        //when
        PromotionApplyResult applyResult = purchaseService.promotionApplyRequest(purchaseRequests).get(0);

        //then
        assertThat(applyResult.state()).isEqualTo(PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 7, 10, 12})
    @DisplayName("일반 결제가 필요한 요청의 state는 PARTIAL_PROMOTION_APPLIED 다")
    void t003(int requiredQuantity) {
        //given
        PurchaseRequest request = new PurchaseRequest("콜라", requiredQuantity);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        //when
        PromotionApplyResult applyResult = purchaseService.promotionApplyRequest(purchaseRequests).get(0);

        //then
        assertThat(applyResult.state()).isEqualTo(PromotionApplyState.PARTIAL_PROMOTION_APPLIED);
    }

}