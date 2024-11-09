package store.controller;

import static global.utils.StringParser.parseShoppingList;

import java.util.ArrayList;
import java.util.List;
import product.dto.response.ProductInfo;
import store.dto.request.PurchaseRequest;
import store.service.StoreService;
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

    public List<PurchaseRequest> shoppingRequest() {
        String requestInput = storeInputView.inputShoppingList();
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        try {
            purchaseRequests = parseShoppingList(requestInput);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            shoppingRequest();
        }
        return purchaseRequests;
    }

    public void checkPurchaseRequest(List<PurchaseRequest> purchaseRequests) {
        try {
            storeService.checkStorageStatus(purchaseRequests);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            shoppingRequest();
        }
    }

    public void purchase(List<PurchaseRequest> purchaseRequests) {
        storeService.purchase(purchaseRequests);
    }
}
