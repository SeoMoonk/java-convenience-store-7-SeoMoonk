package store.dto.response;

import product.entity.Product;
import promotion.entity.Promotion;

public record FinalPurchase(
        String productName,
        int quantity,
        int price
) {
}
