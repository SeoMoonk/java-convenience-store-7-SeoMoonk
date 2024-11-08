package promotion.service;

import static org.assertj.core.api.Assertions.assertThat;

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
        promotionService.setUp(testFilePath);

        //then
        Optional<Promotion> maybePromotion = promotionService.getByName("testName");
        assertThat(maybePromotion.isPresent()).isTrue();
        assertThat(maybePromotion.get().getName()).isEqualTo("testName");
        assertThat(maybePromotion.get().getConditionQuantity()).isEqualTo(2);
    }

}