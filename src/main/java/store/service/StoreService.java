package store.service;

import static product.constants.ProductStatic.PRODUCT_FILE_PATH;
import static promotion.constants.PromotionStatic.PROMOTION_FILE_PATH;

import java.util.ArrayList;
import java.util.List;
import product.dto.ProductInfo;
import product.entity.Product;
import product.service.ProductService;
import promotion.dto.response.PromotionApplyResult;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;
import store.dto.FinalBonus;
import store.dto.FinalPurchase;
import store.dto.response.ReceiptItems;
import store.dto.response.ReceiptPriceInfo;

public class StoreService {

    private final PromotionService promotionService;
    private final ProductService productService;

    public StoreService(PromotionService promotionService, ProductService productService) {
        this.promotionService = promotionService;
        this.productService = productService;
    }

    public void setUp() {
        productService.vacateRepository();
        promotionService.vacateRepository();
        promotionService.loadFromFilePath(PROMOTION_FILE_PATH);
        productService.loadFromFilePath(PRODUCT_FILE_PATH);
    }

    public List<ProductInfo> getProductInfos() {
        return productService.getProductInfos();
    }

    public ReceiptItems processingReceiptItems(List<PromotionApplyResult> promotionResults,
                                               List<PurchaseRequest> normalRequests) {
        ReceiptItems promotionReceiptItems = convertToReceiptItemByPromotion(promotionResults);
        ReceiptItems normalReceiptItems = convertToReceiptItemByNormal(normalRequests);

        return integrateReceiptItems(promotionReceiptItems, normalReceiptItems);
    }

    private ReceiptItems convertToReceiptItemByPromotion(List<PromotionApplyResult> promotionResults) {
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

    private ReceiptItems convertToReceiptItemByNormal(List<PurchaseRequest> normalRequests) {
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

    public ReceiptPriceInfo processingReceiptPriceInfo(ReceiptItems items, boolean isContainsMembershipDiscount) {
        List<FinalPurchase> purchases = items.purchases();
        List<FinalBonus> bonuses = items.bonuses();
        int totalQuantity = calcTotalQuantity(purchases);
        int promotionDiscount = calcPromotionDiscountAmount(bonuses);
        int totalAmount = calcTotalAmount(purchases, bonuses) - promotionDiscount;
        int memberShipDiscount = 0;
        if (isContainsMembershipDiscount) {
            memberShipDiscount = promotionService.calcMembershipDiscountAmount(totalAmount, promotionDiscount);
        }
        int requiredAmount = totalAmount - promotionDiscount - memberShipDiscount;
        return new ReceiptPriceInfo(totalQuantity, totalAmount, promotionDiscount, memberShipDiscount, requiredAmount);
    }

    private int calcTotalQuantity(List<FinalPurchase> purchases) {
        return purchases.stream()
                .mapToInt(FinalPurchase::quantity)
                .sum();
    }

    private int calcTotalAmount(List<FinalPurchase> purchases, List<FinalBonus> bonuses) {
        int purchaseAmount = purchases.stream().mapToInt(FinalPurchase::price).sum();
        int discountAmount = bonuses.stream().mapToInt(FinalBonus::discountAmount).sum();
        return purchaseAmount + discountAmount;
    }

    private int calcPromotionDiscountAmount(List<FinalBonus> bonuses) {
        return bonuses.stream()
                .mapToInt(FinalBonus::discountAmount)
                .sum();
    }
}
