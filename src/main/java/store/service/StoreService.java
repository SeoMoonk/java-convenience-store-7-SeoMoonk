package store.service;

import static product.constants.ProductStatic.PRODUCT_FILE_PATH;
import static promotion.constants.PromotionStatic.PROMOTION_FILE_PATH;
import static store.constants.StoreErrorCode.CANNOT_REQUEST_OVER_STORED_QUANTITY;

import java.util.List;
import product.dto.ProductInfo;
import product.service.ProductService;
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

    public void checkPurchaseRequests(List<PurchaseRequest> purchaseRequests) {
        for (PurchaseRequest request : purchaseRequests) {
            productService.getByName(request.productName());
            int storedQuantity = productService.getAllQuantityByName(request.productName());
            if (request.quantity() > storedQuantity) {
                throw new IllegalArgumentException(CANNOT_REQUEST_OVER_STORED_QUANTITY.getMsgWithPrefix());
            }
        }
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
        int amount = 0;
        for (FinalPurchase purchase : purchases) {
            amount += purchase.quantity();
        }
        return amount;
    }

    private int calcTotalAmount(List<FinalPurchase> purchases, List<FinalBonus> bonuses) {
        int amount = 0;
        for (FinalPurchase p : purchases) {
            amount = amount + p.price();
        }
        for (FinalBonus b : bonuses) {
            amount = amount + b.discountAmount();
        }
        return amount;
    }

    private int calcPromotionDiscountAmount(List<FinalBonus> bonuses) {
        int amount = 0;
        for (FinalBonus b : bonuses) {
            amount = amount + b.discountAmount();
        }
        return amount;
    }
}
