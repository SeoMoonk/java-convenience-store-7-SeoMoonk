package promotion.dto;

public record PromotionApplyInfo(
        int stored,
        int condition,
        int bonus,
        int applyCondition,
        int requiredApplyCount,
        int realApplyCount,
        int tempPurchaseQuantity,
        int tempBonusQuantity,
        int needPurchase
) {
}
