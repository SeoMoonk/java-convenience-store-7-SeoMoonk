package store.controller;

import java.util.List;
import java.util.Map;
import product.dto.response.ProductInfo;
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
        Map<String, Integer> productSets = ItemParser.parseItems(inputItems);

        System.out.println(productSets);
    }
}
