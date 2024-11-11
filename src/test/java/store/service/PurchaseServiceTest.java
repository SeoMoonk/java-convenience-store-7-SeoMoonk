package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static store.constants.StoreErrorCode.CANNOT_REQUEST_OVER_STORED_QUANTITY;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import product.entity.Product;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import product.service.ProductService;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;
import store.dto.request.SeparatedPurchaseRequest;

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
        PurchaseRequest request = new PurchaseRequest("콜라", requiredQuantity);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        PromotionApplyResult applyResult = purchaseService.promotionApplyRequest(purchaseRequests).get(0);

        assertThat(applyResult.state()).isEqualTo(PromotionApplyState.FULL_PROMOTION_APPLIED);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 8})
    @DisplayName("추가 증정이 가능한 요청의 state는 ADDITIONAL_PROMOTION_AVAILABLE 다")
    void t002(int requiredQuantity) {
        PurchaseRequest request = new PurchaseRequest("콜라", requiredQuantity);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        PromotionApplyResult applyResult = purchaseService.promotionApplyRequest(purchaseRequests).get(0);

        assertThat(applyResult.state()).isEqualTo(PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 7, 10, 12})
    @DisplayName("일반 결제가 필요한 요청의 state는 PARTIAL_PROMOTION_APPLIED 다")
    void t003(int requiredQuantity) {
        PurchaseRequest request = new PurchaseRequest("콜라", requiredQuantity);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        PromotionApplyResult applyResult = purchaseService.promotionApplyRequest(purchaseRequests).get(0);

        assertThat(applyResult.state()).isEqualTo(PromotionApplyState.PARTIAL_PROMOTION_APPLIED);
    }

    @Test
    @DisplayName("재고 수량을 초과하는 구매를 요청할 경우 예외가 발생한다")
    void t004() {
        PurchaseRequest request = new PurchaseRequest("콜라", 3000000);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(request));

        assertThatThrownBy(() -> purchaseService.checkPurchaseRequests(purchaseRequests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(CANNOT_REQUEST_OVER_STORED_QUANTITY.getMsgWithPrefix());
    }

    @Test
    @DisplayName("프로모션 대상과 일반 결제 대상을 분리하여 반환할 수 있다")
    void t005() {
        PurchaseRequest promotionRequest = new PurchaseRequest("콜라", 3);
        PurchaseRequest normalRequest = new PurchaseRequest("물", 2);
        List<PurchaseRequest> purchaseRequests = new ArrayList<>(List.of(promotionRequest, normalRequest));

        SeparatedPurchaseRequest separatedPurchaseRequest = purchaseService.separateRequest(purchaseRequests);
        List<PurchaseRequest> promotionRequests = separatedPurchaseRequest.promotionRequests();
        List<PurchaseRequest> normalRequests = separatedPurchaseRequest.normalRequests();

        assertThat(promotionRequests).contains(promotionRequest);
        assertThat(normalRequests).contains(normalRequest);
    }

    @Test
    @DisplayName("프로모션 적용 여부 질문의 응답 내용에 따라 최종 구매할 수량이 변경된다")
    void t006() {
        Product testProduct = productService.getByNameAndHasPromotion("콜라");               //2+1행사 진행중
        PromotionApplyResult request = new PromotionApplyResult(testProduct, 5, 1,
                PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE, 1);     //1개를 더 받을 수있다는 의미

        PromotionApplyResult modifiedApplyResult = purchaseService.applyCustomerAnswer(true, request);

        assertThat(modifiedApplyResult.promotionPurchase()).isEqualTo(5 + 1);
        assertThat(modifiedApplyResult.bonusQuantity()).isEqualTo(1 + 1);
        assertThat(modifiedApplyResult.state()).isEqualTo(PromotionApplyState.FULL_PROMOTION_APPLIED);
    }



}