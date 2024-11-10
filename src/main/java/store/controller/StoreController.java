package store.controller;

import static global.utils.StringParser.parseShoppingList;

import java.util.ArrayList;
import java.util.List;
import product.dto.response.ProductInfo;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import store.dto.request.PurchaseRequest;
import store.dto.request.SeparatedPurchaseRequest;
import store.service.PurchaseService;
import store.service.StoreService;
import store.view.StoreInputView;
import store.view.StoreOutputView;

public class StoreController {

    private final StoreInputView storeInputView;
    private final StoreOutputView storeOutputView;
    private final StoreService storeService;
    private final PurchaseService purchaseService;

    public StoreController(StoreInputView storeInputView, StoreOutputView storeOutputView, StoreService storeService,
                           PurchaseService purchaseService) {
        this.storeInputView = storeInputView;
        this.storeOutputView = storeOutputView;
        this.storeService = storeService;
        this.purchaseService = purchaseService;
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

    public void separateAndTryPurchase(List<PurchaseRequest> purchaseRequests) {
        SeparatedPurchaseRequest separatedPurchaseRequest = purchaseService.separateRequest(purchaseRequests);

        List<PromotionApplyResult> results = purchaseService.promotionApplyRequest(
                separatedPurchaseRequest.promotionRequests());
        checkPromotionApplyResults(results);

    }

    private void checkPromotionApplyResults(List<PromotionApplyResult> results) {
        List<PromotionApplyResult> modifiedResults = new ArrayList<>();

        for(PromotionApplyResult result : results) {
            if(!result.state().isSucceseState()) {
                String input = storeInputView.inputAnswerAboutPromotion(result);
                modifiedResults.add(purchaseService.applyCustomerAnswer(input, result));
                continue;
            }
            modifiedResults.add(result);
        }

        for(PromotionApplyResult result : modifiedResults) {
            StringBuilder sb = new StringBuilder();
            sb.append("이름 : " + result.product().getName() + "\n");
            sb.append("총 차감 갯수 : " + result.promotionPurchase() + "\n");
            sb.append("증정 수량 : " + result.bonusQuantity() + "\n");
            System.out.println(sb);
        };
    }

}