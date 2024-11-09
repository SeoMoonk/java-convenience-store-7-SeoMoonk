package store.controller;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;

import java.util.List;
import product.dto.response.ProductInfo;
import store.dto.request.PurchaseRequest;
import store.service.StoreService;
import store.utils.ItemParser;
import store.view.StoreInputView;
import store.view.StoreOutputView;

public class StoreController {

    private final StoreInputView storeInputView;
    private final StoreOutputView storeOutputView;
    private final StoreService storeService;

    public StoreController(StoreInputView storeInputView, StoreOutputView storeOutputView, StoreService storeService) {
        this.storeInputView = storeInputView;
        this.storeOutputView = storeOutputView;
        this.storeService = storeService;
    }

    public void setUp() {
        storeService.setUp();
    }

    public void visitStore() {
        storeOutputView.printStartMsg();
        List<ProductInfo> productInfos = storeService.getProductInfos();
        storeOutputView.printProductInfos(productInfos);
    }

    public void purchaseRequest() {
        String inputItems = storeInputView.readItems();
        List<PurchaseRequest> purchaseRequests = ItemParser.parseItems(inputItems);
        try {
            storeService.tryPurchase(purchaseRequests);
        } catch (IllegalArgumentException e) {
            System.out.println(ERROR_MSG_PREFIX + e.getMessage());
            purchaseRequest();
        }

        System.out.println("----------판매 후--------");
        List<ProductInfo> productInfos = storeService.getProductInfos();
        storeOutputView.printProductInfos(productInfos);
    }
}
