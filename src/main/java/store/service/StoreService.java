package store.service;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;
import static product.constants.ProductStatic.PRODUCT_FILE_PATH;
import static promotion.constants.PromotionStatic.PROMOTION_FILE_PATH;

import java.util.List;
import product.dto.response.ProductInfo;
import product.dto.response.PurchaseInfo;
import product.service.ProductService;
import promotion.entity.Promotion;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;

public class StoreService {

    private final PromotionService promotionService;
    private final ProductService productService;

    public StoreService(PromotionService promotionService, ProductService productService) {
        this.promotionService = promotionService;
        this.productService = productService;
    }

    public void setUp() {
        promotionService.loadFromFilePath(PROMOTION_FILE_PATH);
        productService.loadFromFilePath(PRODUCT_FILE_PATH);
    }

    public List<ProductInfo> getProductInfos() {
        return productService.getProductInfos();
    }

    public void purchase(List<PurchaseRequest> purchaseRequests) {
        List<PurchaseInfo> purchaseInfos = collectPurchaseInfos(purchaseRequests);
        for (PurchaseInfo info : purchaseInfos) {
            productService.purchase(info);
        }
        /*
        TODO :
         1. 일반 물품 처리
         2. 프로모션 물품 처리
         3. 총 몇 개가 결제되었고, 그 중 몇 개가 프로모션 제품인지 반환
         */

        List<ProductInfo> productInfos = productService.getProductInfos();
        for (ProductInfo info : productInfos) {
            System.out.println(info.toString());
        }
    }

    public void checkStorageStatus(List<PurchaseRequest> purchaseRequests) {
        for (PurchaseRequest request : purchaseRequests) {
            productService.getAllByName(request.productName());
            int storedQuantity = productService.getQuantityByName(request.productName());
            if (storedQuantity < request.quantity()) {
                throw new IllegalArgumentException(
                        ERROR_MSG_PREFIX + "재고의 물량이 부족합니다. 다시 확인해 주세요 : " + request.productName());
            }
        }
    }

    public List<PurchaseInfo> collectPurchaseInfos(List<PurchaseRequest> purchaseRequests) {
        List<Promotion> activePromotions = promotionService.getActivePromotions();
        List<PurchaseInfo> purchaseInfos = productService.getPurchaseInfos(purchaseRequests, activePromotions);
        return purchaseInfos;
    }
}
