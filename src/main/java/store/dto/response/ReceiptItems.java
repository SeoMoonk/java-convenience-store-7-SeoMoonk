package store.dto.response;

import java.util.List;

public record ReceiptItems(
        List<FinalPurchase> purchases,
        List<FinalBonus> bonuses
) {
}
