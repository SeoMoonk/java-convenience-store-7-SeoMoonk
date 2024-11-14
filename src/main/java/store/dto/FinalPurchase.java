package store.dto;


public record FinalPurchase(
        String productName,
        int quantity,
        int price
) {
}
