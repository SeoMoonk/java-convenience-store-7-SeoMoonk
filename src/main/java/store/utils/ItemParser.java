package store.utils;

import static store.constants.StoreStatic.ITEMS_SEPARATOR;
import static store.constants.StoreStatic.ITEM_NAME_QUANTITY_SEPARATOR;
import static store.constants.StoreStatic.ITEM_PREFIX;
import static store.constants.StoreStatic.ITEM_SUFFIX;

import java.util.HashMap;
import java.util.Map;

public class ItemParser {

    public static Map<String, Integer> parseItems(String input) {
        Map<String, Integer> products = new HashMap<>();
        String[] itemDataSets = input.split(ITEMS_SEPARATOR);

        for (String itemSet : itemDataSets) {
            String nameAndQuantity = itemSet.replaceFirst(ITEM_PREFIX, "")
                    .replaceFirst(ITEM_SUFFIX, "");
            String[] nameQuantitySet = nameAndQuantity.split(ITEM_NAME_QUANTITY_SEPARATOR);
            products.put(nameQuantitySet[0], Integer.parseInt(nameQuantitySet[1]));
        }

        return products;
    }
}
