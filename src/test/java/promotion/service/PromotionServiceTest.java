package promotion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import promotion.entity.Promotion;

class PromotionServiceTest {

    private PromotionService promotionService = new PromotionService();

    @Test
    @DisplayName("프로모션 파일을 읽어와 저장소에 저장할 수 있다")
    void t001() {
        //given
        String testFilePath = "src/main/resources/testpromotion.md";

        //when
        promotionService.setUp(testFilePath);

        //then
        Optional<Promotion> OPromotion = promotionService.getByName("testName");
        assertThat(OPromotion.isPresent()).isTrue();
        assertThat(OPromotion.get().getName()).isEqualTo("testName");
        assertThat(OPromotion.get().getBuy()).isEqualTo(2);
    }

}