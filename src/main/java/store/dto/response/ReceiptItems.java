package store.dto.response;

import java.util.List;
import store.dto.FinalBonus;
import store.dto.FinalPurchase;

public record ReceiptItems(
        List<FinalPurchase> purchases,
        List<FinalBonus> bonuses
) {
}
