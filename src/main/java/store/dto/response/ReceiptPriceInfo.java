package store.dto.response;

public record ReceiptPriceInfo(
        int totalQuantity,
        int totalAmount,
        int promotionDiscountAmount,
        int memberShipDiscountAmount,
        int requiredAmount
) {
}