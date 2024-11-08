package store.service;

import static promotion.contants.PromotionStatic.PROMOTION_FILE_PATH;

import promotion.service.PromotionService;

public class StoreService {

    private final PromotionService promotionService;

    public StoreService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setUp() {
        promotionService.setUp(PROMOTION_FILE_PATH);
    }
}
