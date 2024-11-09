package product.dto.response;

public record PurchaseInfo(
        String productName,
        int normalPurchaseQuantity,
        int bonusPurchaseQuantity
) {
}
