package store.service;

import static store.constants.StoreErrorCode.CANNOT_ANSWER_PROCESS;
import static store.constants.StoreErrorCode.CANNOT_REQUEST_OVER_STORED_QUANTITY;

import java.util.ArrayList;
import java.util.List;
import product.entity.Product;
import product.service.ProductService;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import promotion.entity.Promotion;
import promotion.service.PromotionService;
import store.dto.request.PurchaseForm;
import store.dto.request.PurchaseRequest;
import store.dto.request.SeparatedPurchaseRequest;

public class PurchaseService {

    private final ProductService productService;
    private final PromotionService promotionService;

    public PurchaseService(ProductService productService, PromotionService promotionService) {
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public void checkPurchaseRequests(List<PurchaseRequest> purchaseRequests) {
        for (PurchaseRequest request : purchaseRequests) {
            productService.getByName(request.productName());
            int storedQuantity = productService.getAllQuantityByName(request.productName());
            if (request.quantity() > storedQuantity) {
                throw new IllegalArgumentException(CANNOT_REQUEST_OVER_STORED_QUANTITY.getMsgWithPrefix());
            }
        }
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

    public PromotionApplyResult applyCustomerAnswer(boolean isPositiveAnswer, PromotionApplyResult result) {
        if (result.state() == PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE) {
            return fixForAdditionalCondition(isPositiveAnswer, result);
        }
        if (result.state() == PromotionApplyState.PARTIAL_PROMOTION_APPLIED) {
            return fixForPartialCondition(isPositiveAnswer, result);
        }
        throw new IllegalArgumentException(CANNOT_ANSWER_PROCESS.getMsgWithPrefix());
    }

    private PromotionApplyResult fixForAdditionalCondition(boolean isPositiveAnswer, PromotionApplyResult result) {
        if (isPositiveAnswer) {
            return result.applyPromotion();
        }
        return result.excludePromotion();
    }

    private PromotionApplyResult fixForPartialCondition(boolean isPositiveAnswer, PromotionApplyResult result) {
        if (isPositiveAnswer) {
            return result.applyNormalPurchase();
        }
        return result.excludeNormalPurchase();
    }

    public List<PurchaseForm> processingPurChaseRequests(List<PromotionApplyResult> promotionResults,
                                                         List<PurchaseRequest> normalRequests) {
        List<PurchaseForm> purchaseForms = new ArrayList<>();
        purchaseForms.addAll(getPurchaseFormByPromotion(promotionResults));
        purchaseForms.addAll(getPurchaseFormByNormal(normalRequests));
        return purchaseForms;
    }

    public List<PurchaseForm> getPurchaseFormByPromotion(List<PromotionApplyResult> promotionResults) {
        List<PurchaseForm> purchaseForms = new ArrayList<>();
        for (PromotionApplyResult result : promotionResults) {
            Product targetProduct = result.product();
            int requiredQuantity = result.promotionPurchase();
            if (result.product().getQuantity() < result.promotionPurchase()) {
                purchaseForms.addAll(separatedPurchaseFormForPromotion(targetProduct, requiredQuantity));
                continue;
            }
            purchaseForms.add(new PurchaseForm(result.product(), result.promotionPurchase()));
        }
        return purchaseForms;
    }

    private List<PurchaseForm> separatedPurchaseFormForPromotion(Product product, int requiredQuantity) {
        List<PurchaseForm> purchaseForms = new ArrayList<>();
        Product nonePromotionProduct = productService.getByNameAndNotHasPromotion(product.getName());
        purchaseForms.add(new PurchaseForm(product, product.getQuantity()));
        purchaseForms.add(new PurchaseForm(nonePromotionProduct
                , calcRemainingQuantity(requiredQuantity, product.getQuantity())));
        return purchaseForms;
    }

    public List<PurchaseForm> getPurchaseFormByNormal(List<PurchaseRequest> normalRequests) {
        List<PurchaseForm> purchaseForms = new ArrayList<>();
        for (PurchaseRequest request : normalRequests) {
            Product normalProduct = productService.getByNameAndNotHasPromotion(request.productName());
            int requiredQuantity = request.quantity();
            if (normalProduct.getQuantity() < requiredQuantity) {
                purchaseForms.addAll(separatedPurchaseFormForNormal(normalProduct, requiredQuantity));
                continue;
            }
            purchaseForms.add(new PurchaseForm(normalProduct, requiredQuantity));
        }
        return purchaseForms;
    }

    private List<PurchaseForm> separatedPurchaseFormForNormal(Product normalProduct, int requiredQuantity) {
        List<PurchaseForm> purchaseForms = new ArrayList<>();
        int normalQuantity = normalProduct.getQuantity();
        Product promotionProduct = productService.getByNameAndHasPromotion(normalProduct.getName());
        purchaseForms.add(new PurchaseForm(normalProduct, normalProduct.getQuantity()));
        purchaseForms.add(new PurchaseForm(promotionProduct,
                calcRemainingQuantity(requiredQuantity, normalQuantity)));
        return purchaseForms;
    }

    public void purchase(List<PurchaseForm> purchaseForms) {
        productService.purchase(purchaseForms);
    }

    private int calcRemainingQuantity(int requiredQuantity, int quantityForPromotion) {
        return requiredQuantity - quantityForPromotion;
    }
}
