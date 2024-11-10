package store.dto.response;

import java.util.List;

public record ReceipItems(
        List<FinalPurchase> purchases,
        List<FinalBonus> bonuses
) {
}
