package promotion.dto.response;

import static promotion.constants.PromotionApplyState.FULL_PROMOTION_APPLIED;

import product.entity.Product;
import promotion.constants.PromotionApplyState;

public record PromotionApplyResult(
        Product product,
        int promotionPurchase,      //프로모션 재고에서 몇개를 차감해야 되는지
        int bonusQuantity,          //이 결제로 인해 몇 개가 증정으로 처리되는지
        PromotionApplyState state,  //상태 코드
        int conditionalQuantity     //상태에 따른 조건을 위한 갯수
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
