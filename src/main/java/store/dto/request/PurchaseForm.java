package store.dto.request;

import product.entity.Product;

public record PurchaseForm(
        Product product,
        int quantity
) {
}
