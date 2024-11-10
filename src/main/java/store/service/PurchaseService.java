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
import store.dto.request.PurchaseForm;
import store.dto.response.FinalBonus;
import store.dto.response.FinalPurchase;
import store.dto.response.ReceipItems;

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

    public ReceipItems processingReceiptItems(List<PromotionApplyResult> promotionResults,
                                         List<PurchaseRequest> normalRequests) {
        ReceipItems promotionReceipItems = convertToReceiptItemByPromotion(promotionResults);
        ReceipItems normalReceipItems = convertToReceiptItemByNormal(normalRequests);

        return integrateReceiptItems(promotionReceipItems, normalReceipItems);
    }

    public ReceipItems convertToReceiptItemByPromotion(List<PromotionApplyResult> promotionResults) {
        List<FinalPurchase> finalPurchases = new ArrayList<>();
        List<FinalBonus> finalBonuses = new ArrayList<>();
        for (PromotionApplyResult result : promotionResults) {
            Product product = result.product();
            int quantity = result.promotionPurchase();
            int bonusQuantity = result.bonusQuantity();
            int price = product.getPrice();
            finalPurchases.add(
                    new FinalPurchase(product.getName(), quantity, calculateTotalPrice(price, quantity)));
            finalBonuses.add(
                    new FinalBonus(product.getName(), bonusQuantity, calculateTotalPrice(price, bonusQuantity)));
        }
        return new ReceipItems(finalPurchases, finalBonuses);
    }

    public ReceipItems convertToReceiptItemByNormal(List<PurchaseRequest> normalRequests) {
        List<FinalPurchase> finalPurchases = new ArrayList<>();
        for (PurchaseRequest request : normalRequests) {
            String productName = request.productName();
            Product product = productService.getByNameAndNotHasPromotion(productName);
            int quantity = request.quantity();
            finalPurchases.add(new FinalPurchase(request.productName(), request.quantity(),
                    calculateTotalPrice(product.getPrice(), quantity)));
        }
        return new ReceipItems(finalPurchases, null);
    }

    private int calculateTotalPrice(int price, int quantity) {
        return price * quantity;
    }

    private ReceipItems integrateReceiptItems(ReceipItems promotionReceipItems, ReceipItems normalReceipItems) {
        List<FinalPurchase> integratedPurchases = new ArrayList<>();
        integratedPurchases.addAll(promotionReceipItems.purchases());
        integratedPurchases.addAll(normalReceipItems.purchases());

        return new ReceipItems(integratedPurchases, promotionReceipItems.bonuses());
    }
}
