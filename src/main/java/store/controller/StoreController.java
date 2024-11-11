package store.controller;

import static global.utils.StringParser.parseShoppingList;
import static store.constants.StoreStatic.POSITIVE_ANSWER;
import static store.utils.StoreValidator.validateAnswerForAdditionalQuestion;

import java.util.ArrayList;
import java.util.List;
import product.dto.ProductInfo;
import promotion.dto.response.PromotionApplyResult;
import store.dto.request.ProcessedPurchaseRequest;
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

    public StoreController(StoreInputView storeInputView, StoreOutputView storeOutputView,
                           StoreService storeService, PurchaseService purchaseService) {
        this.storeInputView = storeInputView;
        this.storeOutputView = storeOutputView;
        this.storeService = storeService;
        this.purchaseService = purchaseService;
    }

    public void setUp() {
        storeService.setUp();
    }

    public void shopping() {
        do {
            ProcessedPurchaseRequest processedPurchaseRequest = chooseProducts();
            confirmOrder(processedPurchaseRequest);
            provideReceipt(processedPurchaseRequest);
        } while (questionForAdditionalPurchase());
    }

    private ProcessedPurchaseRequest chooseProducts() {
        visitStore();
        List<PurchaseRequest> purchaseRequests = shoppingRequest();
        SeparatedPurchaseRequest separatedRequests = validateAndSeparateUserPurchaseRequest(purchaseRequests);
        List<PromotionApplyResult> applyPromotions = applyActivePromotions(separatedRequests.promotionRequests());
        return new ProcessedPurchaseRequest(separatedRequests.normalRequests(), applyPromotions);
    }

    private void confirmOrder(ProcessedPurchaseRequest processedPurchaseRequest) {
        List<PurchaseForm> purchaseForms = confirmFinalPurchaseForm(
                processedPurchaseRequest.promotionAppliedRequests(), processedPurchaseRequest.normalRequests());
        purchase(purchaseForms);
    }

    private void provideReceipt(ProcessedPurchaseRequest processedPurchaseRequest) {
        ReceiptItems receiptItems = collectReceiptItems(processedPurchaseRequest.promotionAppliedRequests(),
                processedPurchaseRequest.normalRequests());
        ReceiptPriceInfo receiptPriceInfo = processingReceiptPriceInfo(receiptItems,
                isContainsMembershipDiscount());
        printReceipt(receiptItems, receiptPriceInfo);
    }

    private void visitStore() {
        storeOutputView.printStartMsg();
        List<ProductInfo> productInfos = storeService.getProductInfos();
        storeOutputView.printProductInfos(productInfos);
    }

    private List<PurchaseForm> confirmFinalPurchaseForm(List<PromotionApplyResult> promotionPurchases,
                                                        List<PurchaseRequest> normalPurchases) {
        return purchaseService.processingPurChaseRequests(promotionPurchases, normalPurchases);
    }

    private List<PromotionApplyResult> applyActivePromotions(List<PurchaseRequest> promotionRequests) {
        return modifyPromotionAppliesByQuestion(purchaseService.promotionApplyRequest(promotionRequests));
    }

    private SeparatedPurchaseRequest validateAndSeparateUserPurchaseRequest(List<PurchaseRequest> purchaseRequests) {
        checkPurchaseRequests(purchaseRequests);
        return getSeparatedPurchaseRequest(purchaseRequests);
    }

    private List<PurchaseRequest> shoppingRequest() {
        try {
            String requestInput = storeInputView.inputShoppingList();
            return parseShoppingList(requestInput);
        } catch (Exception e) {
            storeOutputView.printErrorMsg(e.getMessage());
            return shoppingRequest();
        }
    }

    private void checkPurchaseRequests(List<PurchaseRequest> purchaseRequests) {
        try {
            purchaseService.checkPurchaseRequests(purchaseRequests);
        } catch (Exception e) {
            storeOutputView.printErrorMsg(e.getMessage());
            checkPurchaseRequests(shoppingRequest());
        }
    }

    private SeparatedPurchaseRequest getSeparatedPurchaseRequest(List<PurchaseRequest> purchaseRequests) {
        return purchaseService.separateRequest(purchaseRequests);
    }

    private ReceiptItems collectReceiptItems(List<PromotionApplyResult> promotionApplyResults,
                                             List<PurchaseRequest> normalRequests) {
        return storeService.processingReceiptItems(promotionApplyResults, normalRequests);
    }

    private void purchase(List<PurchaseForm> purchaseForms) {
        purchaseService.purchase(purchaseForms);
    }

    private List<PromotionApplyResult> modifyPromotionAppliesByQuestion(List<PromotionApplyResult> results) {
        List<PromotionApplyResult> modifiedResults = new ArrayList<>();
        for (PromotionApplyResult result : results) {
            if (!result.state().isSucceseState()) {
                modifiedResults.add(purchaseService.applyCustomerAnswer(questionForApplyPromotion(result), result));
                continue;
            }
            modifiedResults.add(result);
        }
        return modifiedResults;
    }

    private boolean questionForApplyPromotion(PromotionApplyResult result) {
        String input;
        try {
            input = storeInputView.inputAnswerAboutPromotion(result);
            validateAnswerForAdditionalQuestion(input);
        } catch (Exception e) {
            storeOutputView.printErrorMsg(e.getMessage());
            return questionForApplyPromotion(result);
        }
        return input.equals(POSITIVE_ANSWER);
    }

    private boolean questionForAdditionalPurchase() {
        String input;
        try {
            input = storeInputView.inputAnswerAboutAdditionalPurchase();
            validateAnswerForAdditionalQuestion(input);
        } catch (Exception e) {
            storeOutputView.printErrorMsg(e.getMessage());
            return questionForAdditionalPurchase();
        }
        return input.equals(POSITIVE_ANSWER);
    }

    public ReceiptPriceInfo processingReceiptPriceInfo(ReceiptItems receiptItems,
                                                       boolean isContainsMembershipDiscount) {
        return storeService.processingReceiptPriceInfo(receiptItems, isContainsMembershipDiscount);
    }

    public boolean isContainsMembershipDiscount() {
        String input;
        try {
            input = storeInputView.inputAnswerAboutMembership();
            validateAnswerForAdditionalQuestion(input);
        } catch (Exception e) {
            storeOutputView.printErrorMsg(e.getMessage());
            return isContainsMembershipDiscount();
        }
        return input.equals(POSITIVE_ANSWER);
    }

    public void printReceipt(ReceiptItems receiptItems, ReceiptPriceInfo receiptPriceInfo) {
        storeOutputView.printReceipt(receiptItems, receiptPriceInfo);
    }
}