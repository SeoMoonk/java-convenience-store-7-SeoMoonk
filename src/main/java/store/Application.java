package store;

import product.repository.ProductRepository;
import product.repository.ProductRepositoryImpl;
import product.service.ProductService;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;
import promotion.service.PromotionService;
import store.controller.StoreController;
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
        storeController.shopping();
    }
}
