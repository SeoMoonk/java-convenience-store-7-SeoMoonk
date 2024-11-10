package store.controller;

import static global.utils.StringParser.parseShoppingList;

import java.util.ArrayList;
import java.util.List;
import product.dto.response.ProductInfo;
import promotion.dto.response.PromotionApplyResult;
import store.dto.request.PurchaseForm;
import store.dto.request.PurchaseRequest;
import store.dto.request.SeparatedPurchaseRequest;
import store.dto.response.ReceiptItems;
import store.dto.response.ReceiptPriceInfo;
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
        List<PurchaseRequest> purchaseRequests = purchaseRequests = parseShoppingList(requestInput);

        return purchaseRequests;
    }

    public void checkPurchaseRequests(List<PurchaseRequest> purchaseRequests) {
        try {
            storeService.checkPurchaseRequests(purchaseRequests);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            checkPurchaseRequests(shoppingRequest());
        }
    }

    public SeparatedPurchaseRequest getSeparatedPurchaseRequest(List<PurchaseRequest> purchaseRequests) {
        return purchaseService.separateRequest(purchaseRequests);
    }

    public List<PromotionApplyResult> getPromotionApplyResult(List<PurchaseRequest> promotionPurchaseRequests) {
        return modifyPromotionAppliesByQuestion(purchaseService.promotionApplyRequest(promotionPurchaseRequests));
    }

    public List<PurchaseForm> processingPurchaseRequest(List<PromotionApplyResult> promotionApplyResults,
                                                        List<PurchaseRequest> normalRequests) {
        return purchaseService.processingPurChaseRequests(promotionApplyResults, normalRequests);
    }

    public ReceiptItems collectReceiptItems(List<PromotionApplyResult> promotionApplyResults, List<PurchaseRequest> normalRequests) {
        return purchaseService.processingReceiptItems(promotionApplyResults, normalRequests);
    }

    public void purchase(List<PurchaseForm> purchaseForms) {
        purchaseService.purchase(purchaseForms);
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

    public ReceiptPriceInfo processingReceiptPriceInfo(ReceiptItems receiptItems, boolean isContainsMembershipDiscount) {
        return storeService.processingReceiptPriceInfo(receiptItems, isContainsMembershipDiscount);
    }

    public boolean isContainsMembershipDiscount() {
        String input;
        try {
            input = storeInputView.inputAnswerAboutMembership();
            //TODO: 입력 유효성 검사
        } catch(Exception e) {
            System.out.println(e.getMessage());
            isContainsMembershipDiscount();
        }
        return false;
    }
}