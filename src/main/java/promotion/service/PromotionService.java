package promotion.service;

import static global.utils.FileParser.parsingByFilePath;
import static global.utils.StringParser.parseDate;
import static global.utils.StringParser.parseInt;
import static promotion.contants.PromotionStatic.PRESET_KEY_COUNT;
import static promotion.contants.PromotionStatic.getPresetPromotionKeys;

import global.dto.response.FileParsedResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import promotion.entity.Promotion;
import promotion.repository.PromotionRepository;

public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void setUp(String promotionFilePath) {
        FileParsedResponse promotionData = parsingByFilePath(promotionFilePath);
        promotionKeysValidate(promotionData.keys());

        List<List<String>> promotionValues = promotionData.values();
        for(List<String> valueItem : promotionValues) {
            createByValueItem(valueItem);
        }
    }
    
    //FIXME: 리팩토링 필요
    private void promotionKeysValidate(List<String> keys) {
        List<String> presetPromotionKeys = getPresetPromotionKeys();
        keysCountValidate(keys.size());
        keysContainsValidate(keys, presetPromotionKeys);
        keysOrderValidate(keys, presetPromotionKeys);
    }

    private void keysCountValidate(int keyCount) {
        if(keyCount != PRESET_KEY_COUNT) {
            throw new IllegalArgumentException("파일의 키 값이 %d개 여야 합니다".formatted(PRESET_KEY_COUNT));
        }
    }

    private void keysContainsValidate(List<String> keys, List<String> presetKeys) {
        for (String presetKey : presetKeys) {
            if (!keys.contains(presetKey)) {
                throw new IllegalArgumentException("파일에서 사전 설정 키 값을 찾을 수 없습니다" + presetKey);
            }
        }
    }

    private void keysOrderValidate(List<String> keys, List<String> presetKeys) {
        for (int i = 0; i < keys.size(); i++) {
            if (!keys.get(i).equals(presetKeys.get(i))) {
                throw new IllegalArgumentException("파일의 데이터 키 순서가 일치하지 않습니다");
            }
        }
    }

    private void createByValueItem(List<String> valueItem) {
        String name = valueItem.get(0);
        int conditionQuantity = parseInt(valueItem.get(1));
        int bonusQuantity = parseInt(valueItem.get(2));
        LocalDate startDate = parseDate(valueItem.get(3));
        LocalDate endDate = parseDate(valueItem.get(4));
        Promotion promotion = new Promotion(name, conditionQuantity, bonusQuantity, startDate, endDate);
        promotionRepository.save(promotion);
    }

    public Optional<Promotion> getByName(String name) {
        return promotionRepository.findByName(name);
    }
}
