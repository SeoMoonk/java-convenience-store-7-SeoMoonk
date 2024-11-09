package promotion.service;

import static global.utils.FileParser.parsingByFilePath;
import static global.utils.StringParser.parseDate;
import static global.utils.StringParser.parseInt;

import camp.nextstep.edu.missionutils.DateTimes;
import global.constants.FileType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import promotion.constants.PromotionPresetKeys;
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
            //FIXME: 예외 유형 확인
            throw new IllegalArgumentException("해당 이름을 가진 프로모션을 찾을 수 없습니다 : " + name);
        }
        return maybePromotion.get();
    }

    public List<Promotion> getActivePromotions() {
        LocalDate today = DateTimes.now().toLocalDate();
        return promotionRepository.findAllByStartDateBeforeAndEndDateAfter(today);
    }
}
