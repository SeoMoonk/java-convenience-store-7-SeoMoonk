package store.controller;

import static global.utils.StringParser.parseShoppingList;

import java.util.ArrayList;
import java.util.List;
import product.dto.response.ProductInfo;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import store.dto.request.PurchaseForm;
import store.dto.request.PurchaseRequest;
import store.dto.request.SeparatedPurchaseRequest;
import store.dto.response.FinalBonus;
import store.dto.response.FinalPurchase;
import store.dto.response.Receipt;
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

    public List<PurchaseForm> processingPurchaseRequest(List<PurchaseRequest> purchaseRequests) {
        SeparatedPurchaseRequest separatedRequest = purchaseService.separateRequest(purchaseRequests);

        List<PromotionApplyResult> promotionApplyResults = modifyPromotionAppliesByQuestion(
                purchaseService.promotionApplyRequest(separatedRequest.promotionRequests()));

        //TODO: 나중에 메서드 분리해서 변수들 가져올 수 있게 만들고, 영수증 출력에 사용
//        Receipt receipt = purchaseService.processingPurChaseRequests(promotionResults,
//                separatedRequest.normalRequests());

        return purchaseService.processingPurChaseRequests(promotionApplyResults, separatedRequest.normalRequests());
    }

    public void purchase(List<PurchaseForm> purchaseForms) {
        purchaseService.purchase(purchaseForms);

        List<ProductInfo> productInfos = storeService.getProductInfos();
        storeOutputView.printProductInfos(productInfos);
    }

    private List<PromotionApplyResult> modifyPromotionAppliesByQuestion(List<PromotionApplyResult> results) {
        List<PromotionApplyResult> modifiedResults = new ArrayList<>();

        for (PromotionApplyResult result : results) {
            if (!result.state().isSucceseState()) {
                String input = questionForApplyPromotion(result);
                modifiedResults.add(purchaseService.applyCustomerAnswer(input, result));
                continue;
            }
            modifiedResults.add(result);
        }
        return modifiedResults;
    }

    private String questionForApplyPromotion(PromotionApplyResult result) {
        String input;
        try {
            input = storeInputView.inputAnswerAboutPromotion(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return questionForApplyPromotion(result);
        }
        return input;
    }
}