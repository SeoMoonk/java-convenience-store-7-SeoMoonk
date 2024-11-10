package store.dto.response;

public record ReceiptPriceInfo(
        int totalAmount,
        int promotionDiscountAmount,
        int memberShipDiscountAmount,
        int requiredAmount
) {
}