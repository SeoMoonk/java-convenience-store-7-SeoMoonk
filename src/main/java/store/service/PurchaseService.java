package store.service;

import java.util.ArrayList;
import java.util.List;
import product.entity.Product;
import product.service.ProductService;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import promotion.entity.Promotion;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;
import store.dto.request.SeparatedPurchaseRequest;

public class PurchaseService {

    private final ProductService productService;
    private final PromotionService promotionService;

    public PurchaseService(ProductService productService, PromotionService promotionService) {
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public SeparatedPurchaseRequest separateRequest(List<PurchaseRequest> requests) {
        List<Promotion> activePromotions = promotionService.getActivePromotions();
        List<PurchaseRequest> promotionRequests = new ArrayList<>();
        List<PurchaseRequest> normalRequests = new ArrayList<>();

        for (PurchaseRequest request : requests) {
            if (productService.isPromotionTargetRequest(activePromotions, request)) {
                promotionRequests.add(request);
                continue;
            }
            normalRequests.add(request);
        }

        return new SeparatedPurchaseRequest(promotionRequests, normalRequests);
    }

    public List<PromotionApplyResult> promotionApplyRequest(List<PurchaseRequest> promotionRequests) {

        List<PromotionApplyResult> results = new ArrayList<>();

        for (PurchaseRequest request : promotionRequests) {
            Product product = productService.getByNameAndHasPromotion(request.productName());
            results.add(promotionService.getPromotionApplyResult(product, request.quantity()));
        }

        return results;
    }

    public PromotionApplyResult applyCustomerAnswer(String input, PromotionApplyResult result) {

        if (result.state() == PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE) {
            return fixForAdditionalCondition(input, result);
        }

        if (result.state() == PromotionApplyState.PARTIAL_PROMOTION_APPLIED) {
            return fixForPartialCondition(input, result);
        }

        throw new IllegalArgumentException("프로모션 요청이 잘못되었습니다");
    }

    private PromotionApplyResult fixForAdditionalCondition(String input, PromotionApplyResult result) {

        if (input.equals("Y")) {
            return result.applyPromotion();
        }

        if (input.equals("N")) {
            return result.excludePromotion();
        }

        //FIXME: 리팩토링
        throw new IllegalArgumentException("질문에 대한 응답은 (Y/N) 으로만 응답해야 합니다" + input);
    }

    private PromotionApplyResult fixForPartialCondition(String input, PromotionApplyResult result) {

        if (input.equals("Y")) {
            return result.applyNormalPurchase();
        }

        if (input.equals("N")) {
            return result.excludeNormalPurchase();
        }

        //FIXME: 리팩토링
        throw new IllegalArgumentException("질문에 대한 응답은 (Y/N) 으로만 응답해야 합니다" + input);
    }
}
