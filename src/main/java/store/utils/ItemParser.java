package store.utils;

import static store.constants.StoreStatic.ITEMS_SEPARATOR;
import static store.constants.StoreStatic.ITEM_NAME_QUANTITY_SEPARATOR;
import static store.constants.StoreStatic.ITEM_PREFIX;
import static store.constants.StoreStatic.ITEM_SUFFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.dto.request.PurchaseRequest;

public class ItemParser {

    public static List<PurchaseRequest> parseItems(String input) {
        List<PurchaseRequest> purchaseRequests = new ArrayList<>();
        String[] itemDataSets = input.split(ITEMS_SEPARATOR);
        for (String itemSet : itemDataSets) {
            String nameAndQuantity = itemSet.replaceFirst(ITEM_PREFIX, "")
                    .replaceFirst(ITEM_SUFFIX, "");
            String[] nameQuantitySet = nameAndQuantity.split(ITEM_NAME_QUANTITY_SEPARATOR);
            purchaseRequests.add(new PurchaseRequest(nameQuantitySet[0], Integer.parseInt(nameQuantitySet[1])));
        }
        return purchaseRequests;
    }
}
