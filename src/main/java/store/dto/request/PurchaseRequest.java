package store.dto.request;

public record PurchaseRequest(
        String productName,
        int quantity
) {
}
