package store.dto.request;

import java.util.List;

public record SeparatedPurchaseRequest(
        List<PurchaseRequest> promotionRequests,
        List<PurchaseRequest> normalRequests
) {
}
