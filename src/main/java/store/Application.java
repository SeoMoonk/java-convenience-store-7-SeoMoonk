package store;

import java.util.List;
import product.dto.response.ProductInfo;
import product.entity.Product;
import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import product.service.ProductService;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;
import store.controller.StoreController;
import store.dto.request.PurchaseForm;
import store.dto.request.PurchaseRequest;
import store.service.PurchaseService;
import store.service.StoreService;
import store.view.StoreInputView;
import store.view.StoreOutputView;

public class Application {

    private static final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private static final PromotionService promotionService = new PromotionService(promotionRepository);
    private static final ProductRepository productRepository = new ProductRepositoryImpl();
    private static final ProductService productService = new ProductService(promotionService, productRepository);
    private static final StoreInputView storeInputView = new StoreInputView();
    private static final StoreOutputView storeOutputView = new StoreOutputView();
    private static final StoreService storeService = new StoreService(promotionService, productService);
    private static final PurchaseService purchaseService = new PurchaseService(productService, promotionService);
    private static final StoreController storeController = new StoreController(storeInputView, storeOutputView,
            storeService, purchaseService);

    public static void main(String[] args) {
        storeController.setUp();
        storeController.visitStore();
        List<PurchaseRequest> purchaseRequests = storeController.shoppingRequest();
        storeController.checkPurchaseRequests(purchaseRequests);
        List<PurchaseForm> purchaseForms = storeController.processingPurchaseRequest(purchaseRequests);
        storeController.purchase(purchaseForms);
//        storeController.getReceipt();
    }
}
