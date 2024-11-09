package product.dto.response;

import product.entity.Product;

public record PurchaseInfo(
        Product product,
        int normalPurchaseQuantity,
        int bonusPurchaseQuantity
) {
}
