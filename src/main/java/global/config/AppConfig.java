package global.config;

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

public class AppConfig {

    private final PromotionRepository promotionRepository;
    private final PromotionService promotionService;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final StoreInputView storeInputView;
    private final StoreOutputView storeOutputView;
    private final StoreService storeService;
    private final PurchaseService purchaseService;
    private final StoreController storeController;

    public AppConfig() {
        this.promotionRepository = new PromotionRepositoryImpl();
        this.promotionService = new PromotionService(promotionRepository);
        this.productRepository = new ProductRepositoryImpl();
        this.productService = new ProductService(promotionService, productRepository);
        this.storeInputView = new StoreInputView();
        this.storeOutputView = new StoreOutputView();
        this.storeService = new StoreService(promotionService, productService);
        this.purchaseService = new PurchaseService(promotionService, productService);
        this.storeController = new StoreController(storeInputView, storeOutputView, storeService, purchaseService);
    }

    public StoreController getStoreController() {
        return storeController;
    }
}
