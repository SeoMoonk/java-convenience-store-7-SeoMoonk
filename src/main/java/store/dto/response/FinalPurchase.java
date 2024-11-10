package store.dto.response;


public record FinalPurchase(
        String productName,
        int quantity,
        int price
) {
}
