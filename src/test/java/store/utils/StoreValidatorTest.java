package store.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static store.constants.StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StoreValidatorTest {

    private final StoreValidator storeValidator = new StoreValidator();

    @ParameterizedTest
    @ValueSource(strings = {"콜라-5,사이다-3", "콜라_5,사이다_3", "[콜라-5]_[사이다-3]"})
    @DisplayName("구매를 요청할 때, 정해진 규격대로 입력하지 않으면, 예외가 발생한다")
    void t001(String input) {
        assertThatThrownBy(() -> storeValidator.validatePurchaseRequest(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
    }

    @ParameterizedTest
    @ValueSource(strings = {"[콜라--5]", "[콜라-0]", "[콜라-03]", "[콜라-]", "[콜라- 5]", "[콜라-하나]", "[콜라-+3]"})
    @DisplayName("구매를 요청할 때, 수량에는 음수나 0과 관련된 오타, 공백, 양수 기호, 숫자가 아닌 것 등이 입력되면 예외가 발생한다")
    void t002(String input) {
        assertThatThrownBy(() -> storeValidator.validatePurchaseRequest(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
    }
}