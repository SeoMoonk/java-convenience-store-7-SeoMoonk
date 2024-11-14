package promotion.dto.response;

import static promotion.constants.PromotionApplyState.FULL_PROMOTION_APPLIED;

import product.entity.Product;
import promotion.constants.PromotionApplyState;

public record PromotionApplyResult(
        Product product,
        int promotionPurchase,
        int bonusQuantity,
        PromotionApplyState state,
        int conditionalQuantity
) {

    public PromotionApplyResult applyPromotion() {
        int finalPromotionPurchase = promotionPurchase + conditionalQuantity;
        int finalBonusQuantity = bonusQuantity + conditionalQuantity;
        return new PromotionApplyResult(product, finalPromotionPurchase, finalBonusQuantity,
                FULL_PROMOTION_APPLIED, 0);
    }

    public PromotionApplyResult excludePromotion() {
        return new PromotionApplyResult(product, promotionPurchase, bonusQuantity,
                FULL_PROMOTION_APPLIED, 0);
    }

    public PromotionApplyResult applyNormalPurchase() {
        int finalPromotionPurchase = promotionPurchase + conditionalQuantity;
        return new PromotionApplyResult(product, finalPromotionPurchase, bonusQuantity,
                FULL_PROMOTION_APPLIED, 0);
    }

    public PromotionApplyResult excludeNormalPurchase() {
        return new PromotionApplyResult(product, promotionPurchase, bonusQuantity,
                FULL_PROMOTION_APPLIED, 0);
    }
}
