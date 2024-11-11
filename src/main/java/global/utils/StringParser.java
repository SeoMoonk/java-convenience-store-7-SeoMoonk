package global.utils;

import static store.constants.StoreStatic.ITEMS_SEPARATOR;
import static store.constants.StoreStatic.ITEM_NAME_QUANTITY_SEPARATOR;
import static store.constants.StoreStatic.ITEM_PREFIX_REGEX;
import static store.constants.StoreStatic.ITEM_SUFFIX_REGEX;
import static store.utils.StoreValidator.validatePurchaseRequest;

import global.constants.GlobalErrorCode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import store.constants.StoreErrorCode;
import store.dto.request.PurchaseRequest;

public class StringParser {

    public static int parseInt(String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
        }
        return number;
    }

    public static LocalDate parseDate(String input) {
        LocalDate date;
        try {
            date = LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(GlobalErrorCode.CANNOT_PARSING_DATETIME.getMsgWithPrefix(),
                    input, e.getErrorIndex());
        }
        return date;
    }

    public static List<PurchaseRequest> parseShoppingList(String input) {
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        String[] itemDataSets = input.split(ITEMS_SEPARATOR);
        for (String itemSet : itemDataSets) {
            validatePurchaseRequest(itemSet);
            String nameAndQuantity = itemSet.replaceFirst(ITEM_PREFIX_REGEX, "")
                    .replaceFirst(ITEM_SUFFIX_REGEX, "");
            String[] nameQuantitySet = nameAndQuantity.split(ITEM_NAME_QUANTITY_SEPARATOR);
            purchaseRequests.add(new PurchaseRequest(nameQuantitySet[0], parseInt(nameQuantitySet[1])));
        }
        return purchaseRequests;
    }
}
