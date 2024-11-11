package global.utils;

import static store.constants.StoreStatic.ITEMS_SEPARATOR;
import static store.constants.StoreStatic.ITEM_NAME_QUANTITY_SEPARATOR;
import static store.constants.StoreStatic.ITEM_PREFIX;
import static store.constants.StoreStatic.ITEM_SUFFIX;
import static store.utils.StoreValidator.validatePurchaseRequest;

import global.constants.GlobalErrorCode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import store.dto.request.PurchaseRequest;

public class StringParser {

    public static int parseInt(String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(GlobalErrorCode.CANNOT_PARSING_NUMBER.getMsgWithPrefix() + " : " + input);
        }
        return number;
    }

    public static LocalDate parseDate(String input) {
        LocalDate date;
        try {
            date = LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(GlobalErrorCode.CANNOT_PARSING_DATETIME.getMsgWithPrefix() + " : " + input,
                    input, e.getErrorIndex());
        }
        return date;
    }

    public static List<PurchaseRequest> parseShoppingList(String input) {
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        String[] itemDataSets = input.split(ITEMS_SEPARATOR);
        for (String itemSet : itemDataSets) {
            validatePurchaseRequest(itemSet);
            String nameAndQuantity = itemSet.replaceFirst(ITEM_PREFIX, "")
                    .replaceFirst(ITEM_SUFFIX, "");
            String[] nameQuantitySet = nameAndQuantity.split(ITEM_NAME_QUANTITY_SEPARATOR);
            purchaseRequests.add(new PurchaseRequest(nameQuantitySet[0], parseInt(nameQuantitySet[1])));
        }
        return purchaseRequests;
    }

}
