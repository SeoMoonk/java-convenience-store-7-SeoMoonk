package store.dto.request;

import java.util.List;
import promotion.dto.response.PromotionApplyResult;

public record ProcessedPurchaseRequest(
        List<PurchaseRequest> normalRequests,
        List<PromotionApplyResult> promotionAppliedRequests
) {
}
