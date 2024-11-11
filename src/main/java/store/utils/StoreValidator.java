package store.utils;

import static global.utils.StringParser.parseInt;
import static store.constants.StoreStatic.ITEMS_SEPARATOR;
import static store.constants.StoreStatic.ITEM_NAME_QUANTITY_SEPARATOR;
import static store.constants.StoreStatic.ITEM_PREFIX;
import static store.constants.StoreStatic.ITEM_SUFFIX;
import static store.constants.StoreStatic.NEGATIVE_ANSWER;
import static store.constants.StoreStatic.POSITIVE_ANSWER;

import store.constants.StoreErrorCode;

public class StoreValidator {

    public static void validatePurchaseRequest(String purchaseRequestItem) {
        purchaseRequestFormatValidate(purchaseRequestItem);
        purchaseRequestQuantityValidate(purchaseRequestItem.split(ITEM_NAME_QUANTITY_SEPARATOR)[1]);
    }

    public static void validateAnswerForAdditionalQuestion(String answer) {
        if(answer.equals(POSITIVE_ANSWER) || answer.equals(NEGATIVE_ANSWER)) {
            throw new IllegalArgumentException(StoreErrorCode.INVALID_INPUT.getMsgWithPrefix());
        }
    }

    private static void purchaseRequestFormatValidate(String purchaseRequestItem) {
        if (!purchaseRequestItem.startsWith(ITEM_PREFIX) || !purchaseRequestItem.endsWith(ITEM_SUFFIX)
                || !purchaseRequestItem.contains(ITEMS_SEPARATOR)) {
            throw new IllegalArgumentException(StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
        }
    }

    private static void purchaseRequestQuantityValidate(String quantityInput) {
        quantityBlankValidate(quantityInput);
        quantityNegativeSignValidate(quantityInput);
        quantityWithZeroValidate(quantityInput);
        quantityWithPlusSignValidate(quantityInput);
        parseInt(quantityInput);
    }

    private static void quantityBlankValidate(String quantityInput) {
        if(quantityInput.contains(" ") || quantityInput.isBlank()) {
            throw new IllegalArgumentException(StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
        }
    }

    private static void quantityNegativeSignValidate(String quantityInput) {
        if(quantityInput.contains("-")) {
            throw new IllegalArgumentException(StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
        }
    }

    private static void quantityWithZeroValidate(String quantityInput) {
        if(quantityInput.startsWith("0")) {
            throw new IllegalArgumentException(StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
        }
    }

    private static void quantityWithPlusSignValidate(String quantityInput) {
        if(quantityInput.startsWith("+")) {
            throw new IllegalArgumentException(StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
        }
    }
}
