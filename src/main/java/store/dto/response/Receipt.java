package store.dto.response;

import java.util.List;

public record Receipt(
        List<FinalPurchase> purchases,
        List<FinalBonus> bonuses
) {
}
