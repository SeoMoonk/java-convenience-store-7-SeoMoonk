package store.service;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;
import static product.constants.ProductStatic.PRODUCT_FILE_PATH;
import static promotion.constants.PromotionStatic.PROMOTION_FILE_PATH;

import java.util.List;
import product.dto.response.ProductInfo;
import product.entity.Product;
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
                throw new IllegalArgumentException(ERROR_MSG_PREFIX + "재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }
        }
    }

//    public void purchase(List<PurchaseRequest> purchaseRequests) {
//        List<PurchaseInfo> purchaseInfos = collectPurchaseInfos(purchaseRequests);
//        for (PurchaseInfo info : purchaseInfos) {
//            productService.purchase(info);
//        }
//        /*
//        TODO :
//         1. 일반 물품 처리
//         2. 프로모션 물품 처리
//         3. 총 몇 개가 결제되었고, 그 중 몇 개가 프로모션 제품인지 반환
//         */
//
//        List<ProductInfo> productInfos = productService.getProductInfos();
//        for (ProductInfo info : productInfos) {
//            System.out.println(info.toString());
//        }
//    }
//
//    public void requestStorageStatusWithPromotions(List<PurchaseRequest> requests) {
//        List<Promotion> promotions = promotionService.getActivePromotions();
//        productService.checkStorageStatusWithPromotions(requests, promotions);
//    }
//
//    public void getPromotionTargetProducts(List<PurchaseRequest> requests) {
//        List<Promotion> promotions = promotionService.getActivePromotions();
//
//        for (PurchaseRequest request : requests) {
//            productService.isPromotionTargetProduct(promotions, request);
//        }
//    }
//
//    public List<PurchaseInfo> collectPurchaseInfos(List<PurchaseRequest> purchaseRequests) {
//        List<Promotion> activePromotions = promotionService.getActivePromotions();
//        List<PurchaseInfo> purchaseInfos = productService.getPurchaseInfos(purchaseRequests, activePromotions);
//        return purchaseInfos;
//    }
}
