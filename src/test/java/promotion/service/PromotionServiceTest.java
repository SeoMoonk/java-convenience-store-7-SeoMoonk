package promotion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static store.constants.StoreErrorCode.NONE_EXISTENT_PROMOTION;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import product.entity.Product;
import promotion.constants.PromotionApplyState;
import promotion.dto.response.PromotionApplyResult;
import promotion.entity.Promotion;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;

class PromotionServiceTest {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private final PromotionService promotionService = new PromotionService(promotionRepository);

    @BeforeEach
    void setUp() {
        String testFilePath = "src/main/resources/testpromotion.md";
        promotionService.loadFromFilePath(testFilePath);
    }

    @Test
    @DisplayName("프로모션 파일을 읽어와 저장소에 저장할 수 있다")
    void t001() {
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
        List<Promotion> activePromotions = promotionService.getActivePromotions();

        assertThat(activePromotions.get(0).getName()).isEqualTo("testPromotion");
        assertThat(activePromotions.get(1).getName()).isEqualTo("testPromotion3");
    }

    @ParameterizedTest
    @ValueSource(strings = {"없는행사", "한달남은행사", "이미 지난 행사"})
    @DisplayName("없는 프로모션의 이름으로 프로모션을 가져오려고 하면, 예외가 발생한다")
    void t003(String promotionName) {
        assertThatThrownBy(() -> promotionService.getByName(promotionName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(NONE_EXISTENT_PROMOTION.getMsgWithPrefix());
    }

    @Test
    @DisplayName("오늘 날짜 기준으로 활성화된 프로모션들만 가져올 수 있다")
    void t004() {
        LocalDate today = LocalDate.now();

        List<Promotion> activePromotions = promotionService.getActivePromotions();

        for (Promotion p : activePromotions) {
            assertThat(p.getStartDate()).isBeforeOrEqualTo(today);
            assertThat(p.getEndDate()).isAfterOrEqualTo(today);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 6, 9})
    @DisplayName("정확히 프로모션을 모두 적용하여 구매한 경우, 그에 맞는 응답이 반환된다")
    void t005(int requiredQuantity) {
        Promotion promotion = new Promotion("2+1행사", 2, 1,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        Product product = new Product("testProduct", 1000, 20, promotion);

        PromotionApplyResult promotionApplyResult = promotionService.getPromotionApplyResult(product, requiredQuantity);

        assertThat(promotionApplyResult.state()).isEqualTo(PromotionApplyState.FULL_PROMOTION_APPLIED);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 8})
    @DisplayName("프로모션을 적용받을 수 있는 구매 갯수인 경우, 이를 안내하는 상태를 포함하여 반환한다")
    void t006(int requiredQuantity) {
        Promotion promotion = new Promotion("2+1행사", 2, 1,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        Product product = new Product("testProduct", 1000, 20, promotion);

        PromotionApplyResult promotionApplyResult = promotionService.getPromotionApplyResult(product, requiredQuantity);

        assertThat(promotionApplyResult.state()).isEqualTo(PromotionApplyState.ADDITIONAL_PROMOTION_AVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(ints = {12, 15, 18})
    @DisplayName("재고가 부족하여 프로모션 증정을 제공할 수 없다면, 이를 안내하는 상태를 포함하여 반환한다")
    void t007(int requiredQuantity) {
        Promotion promotion = new Promotion("2+1행사", 2, 1,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        Product product = new Product("testProduct", 1000, 10, promotion);

        PromotionApplyResult promotionApplyResult = promotionService.getPromotionApplyResult(product, requiredQuantity);

        assertThat(promotionApplyResult.state()).isEqualTo(PromotionApplyState.PARTIAL_PROMOTION_APPLIED);
    }

    @Test
    @DisplayName("멤버십 할인 금액을 계산할 수 있다")
    void t008() {
        int expectedDiscount = 2400;

        int discountAmount = promotionService.calcMembershipDiscountAmount(10000, 2000);

        assertThat(expectedDiscount).isEqualTo(discountAmount);
    }

    @Test
    @DisplayName("멤버십 할인의 최대 한도는 8000원이다")
    void t009() {
        int expectedDiscount = 8000;

        int discountAmount = promotionService.calcMembershipDiscountAmount(100000, 2000);

        assertThat(expectedDiscount).isEqualTo(discountAmount);
    }

}