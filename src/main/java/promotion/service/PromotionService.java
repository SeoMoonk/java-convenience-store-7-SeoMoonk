package promotion.service;

import static global.utils.FileParser.parsingByFilePath;
import static global.utils.StringParser.parseDate;
import static global.utils.StringParser.parseInt;
import static promotion.constants.PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE;
import static promotion.constants.PromotionApplyState.FULL_PROMOTION_APPLIED;
import static promotion.constants.PromotionApplyState.PARTIAL_PROMOTION_APPLIED;
import static promotion.constants.PromotionStatic.MAXIMUM_MEMBERSHIP_DISCOUNT_LIMIT;
import static promotion.constants.PromotionStatic.MEMBERSHIP_DISCOUNT_RATE;
import static store.constants.StoreErrorCode.NONE_EXISTENT_PROMOTION;

import camp.nextstep.edu.missionutils.DateTimes;
import global.constants.FileType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import product.entity.Product;
import promotion.constants.PromotionPresetKeys;
import promotion.dto.PromotionApplyInfo;
import promotion.dto.response.PromotionApplyResult;
import promotion.entity.Promotion;
import promotion.repository.PromotionRepository;

public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void loadFromFilePath(String promotionFilePath) {
        List<Map<String, String>> promotionDataSets = parsingByFilePath(promotionFilePath, FileType.PROMOTION);
        for (Map<String, String> dataSet : promotionDataSets) {
            createByDataSet(dataSet);
        }
    }

    private void createByDataSet(Map<String, String> dataSet) {
        String name = dataSet.get(PromotionPresetKeys.PROMOTION_NAME_PRESET_KEY.getKey());
        int conditionQuantity = parseInt(
                dataSet.get(PromotionPresetKeys.PROMOTION_CONDITION_QUANTITY_PRESET_KEY.getKey()));
        int bonusQuantity = parseInt(dataSet.get(PromotionPresetKeys.PROMOTION_BONUS_QUANTITY_PRESET_KEY.getKey()));
        LocalDate startDate = parseDate(dataSet.get(PromotionPresetKeys.PROMOTION_START_DATE_PRESET_KEY.getKey()));
        LocalDate endDate = parseDate(dataSet.get(PromotionPresetKeys.PROMOTION_END_DATE_PRESET_KEY.getKey()));

        Promotion promotion = new Promotion(name, conditionQuantity, bonusQuantity, startDate, endDate);
        promotionRepository.save(promotion);
    }

    public Promotion getByName(String name) {
        Optional<Promotion> maybePromotion = promotionRepository.findByName(name);
        if (maybePromotion.isEmpty()) {
            throw new IllegalArgumentException(NONE_EXISTENT_PROMOTION.getMsgWithPrefix());
        }
        return maybePromotion.get();
    }

    public List<Promotion> getActivePromotions() {
        LocalDate today = DateTimes.now().toLocalDate();
        return promotionRepository.findAllByStartDateBeforeAndEndDateAfter(today);
    }

    public PromotionApplyResult getPromotionApplyResult(Product product, int requiredQuantity) {
        PromotionApplyInfo info = generatePromotionApplyInfo(product, requiredQuantity);
        if (canApplyFullPromotion(info)) {
            return new PromotionApplyResult(product, info.tempPurchaseQuantity(), info.tempPurchaseQuantity(),
                    FULL_PROMOTION_APPLIED, 0);
        }
        if (canApplyAdditionalPromotion(info)) {
            return new PromotionApplyResult(product, info.tempPurchaseQuantity() + info.condition(),
                    info.tempBonusQuantity(), ADDITIONAL_PROMOTION_AVAILABLE, info.bonus());
        }
        if (isNeedNormalPurchase(info)) {
            return new PromotionApplyResult(product, info.tempPurchaseQuantity(), info.tempBonusQuantity(),
                    PARTIAL_PROMOTION_APPLIED, info.condition());
        }
        return new PromotionApplyResult(product, info.tempPurchaseQuantity(), info.tempBonusQuantity(),
                PARTIAL_PROMOTION_APPLIED, info.needPurchase());
    }

    private boolean canApplyFullPromotion(PromotionApplyInfo info) {
        if (info.realApplyCount() == info.requiredApplyCount() && info.needPurchase() == 0) {
            return true;
        }
        return false;
    }

    private boolean canApplyAdditionalPromotion(PromotionApplyInfo info) {
        if (info.realApplyCount() >= info.requiredApplyCount() && info.needPurchase() == info.condition()) {
            return hasEnoughQuantity(info);
        }
        if (info.realApplyCount() == info.requiredApplyCount() - 1 && info.needPurchase() == info.condition()) {
            return hasEnoughQuantity(info);
        }
        return false;
    }

    private boolean hasEnoughQuantity(PromotionApplyInfo info) {
        if (info.stored() >= info.tempPurchaseQuantity() + info.bonus()) {
            return true;
        }
        return false;
    }

    private boolean isNeedNormalPurchase(PromotionApplyInfo info) {
        return info.realApplyCount() == info.requiredApplyCount() - 1 && info.needPurchase() == info.condition();
    }

    public PromotionApplyInfo generatePromotionApplyInfo(Product product, int requiredQuantity) {
        Promotion promotion = product.getPromotion();
        int applyCondition = promotion.getApplyCondition();
        int realApplyCount = Math.min(product.getQuantity() / applyCondition, requiredQuantity / applyCondition);

        return new PromotionApplyInfo(
                product.getQuantity(), promotion.getConditionQuantity(), promotion.getBonusQuantity(),
                applyCondition, requiredQuantity / applyCondition, realApplyCount, realApplyCount * applyCondition,
                realApplyCount * promotion.getBonusQuantity(), requiredQuantity - (realApplyCount * applyCondition)
        );
    }

    public void vacateRepository() {
        promotionRepository.removeAll();
    }

    public int calcMembershipDiscountAmount(int totalAmount, int promotionDiscountAmount) {
        int targetAmount = totalAmount - promotionDiscountAmount;
        double result = targetAmount * MEMBERSHIP_DISCOUNT_RATE;
        if (result > MAXIMUM_MEMBERSHIP_DISCOUNT_LIMIT) {
            return MAXIMUM_MEMBERSHIP_DISCOUNT_LIMIT;
        }
        return (int) Math.round(result);
    }
}
