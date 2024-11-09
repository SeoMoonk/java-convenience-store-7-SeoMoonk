package promotion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import promotion.entity.Promotion;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;

class PromotionServiceTest {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private final PromotionService promotionService = new PromotionService(promotionRepository);

    @Test
    @DisplayName("프로모션 파일을 읽어와 저장소에 저장할 수 있다")
    void t001() {
        //given
        String testFilePath = "src/main/resources/testpromotion.md";

        //when
        promotionService.loadFromFilePath(testFilePath);

        //then
        Promotion promotion = promotionService.getByName("testPromotion");
        assertThat(promotion.getName()).isEqualTo("testPromotion");
        assertThat(promotion.getConditionQuantity()).isEqualTo(2);
        assertThat(promotion.getBonusQuantity()).isEqualTo(1);
        assertThat(promotion.getStartDate()).isEqualTo(LocalDate.parse("2024-01-01"));
        assertThat(promotion.getEndDate()).isEqualTo("2024-12-31");
    }

    @Test
    @DisplayName("오늘 활성화중인 프로모션 리스트를 받아올 수 있다")
    void t002() {
        //given
        String testFilePath = "src/main/resources/testpromotion.md";
        promotionService.loadFromFilePath(testFilePath);

        //when
        List<Promotion> activePromotions = promotionService.getActivePromotions();

        //then
        assertThat(activePromotions.get(0).getName()).isEqualTo("testPromotion");
        assertThat(activePromotions.get(1).getName()).isEqualTo("testPromotion3");
    }

}