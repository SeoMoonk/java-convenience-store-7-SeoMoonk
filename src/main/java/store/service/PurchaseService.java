package store.service;

import static store.constants.StoreErrorCode.CANNOT_ANSWER_PROCESS;

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
import store.dto.request.PurchaseForm;
import store.dto.response.FinalBonus;
import store.dto.response.FinalPurchase;
import store.dto.response.ReceiptItems;

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

    private int calcRemainingQuantity(int requiredQuantity, int quantityForPromotion) {
        return requiredQuantity - quantityForPromotion;
    }

    public void purchase(List<PurchaseForm> purchaseForms) {
        productService.purchase(purchaseForms);
    }

    public ReceiptItems processingReceiptItems(List<PromotionApplyResult> promotionResults,
                                               List<PurchaseRequest> normalRequests) {
        ReceiptItems promotionReceiptItems = convertToReceiptItemByPromotion(promotionResults);
        ReceiptItems normalReceiptItems = convertToReceiptItemByNormal(normalRequests);

        return integrateReceiptItems(promotionReceiptItems, normalReceiptItems);
    }

    public ReceiptItems convertToReceiptItemByPromotion(List<PromotionApplyResult> promotionResults) {
        List<FinalPurchase> finalPurchases = new ArrayList<>();
        List<FinalBonus> finalBonuses = new ArrayList<>();
        for (PromotionApplyResult result : promotionResults) {
            Product product = result.product();
            int quantity = result.promotionPurchase();
            int bonusQuantity = result.bonusQuantity();
            int price = product.getPrice();
            finalPurchases.add(new FinalPurchase(product.getName(), quantity, price * quantity));
            finalBonuses.add(new FinalBonus(product.getName(), bonusQuantity, price * bonusQuantity));
        }
        return new ReceiptItems(finalPurchases, finalBonuses);
    }

    public ReceiptItems convertToReceiptItemByNormal(List<PurchaseRequest> normalRequests) {
        List<FinalPurchase> finalPurchases = new ArrayList<>();
        for (PurchaseRequest request : normalRequests) {
            String productName = request.productName();
            Product product = productService.getByNameAndNotHasPromotion(productName);
            finalPurchases.add(new FinalPurchase(request.productName(), request.quantity()
                    , product.getPrice() * request.quantity()));
        }
        return new ReceiptItems(finalPurchases, null);
    }

    private ReceiptItems integrateReceiptItems(ReceiptItems promotionReceiptItems, ReceiptItems normalReceiptItems) {
        List<FinalPurchase> integratedPurchases = new ArrayList<>();
        integratedPurchases.addAll(promotionReceiptItems.purchases());
        integratedPurchases.addAll(normalReceiptItems.purchases());

        return new ReceiptItems(integratedPurchases, promotionReceiptItems.bonuses());
    }
}
