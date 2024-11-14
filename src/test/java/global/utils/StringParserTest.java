package global.utils;

import static global.constants.GlobalErrorCode.CANNOT_PARSING_DATETIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static store.constants.StoreErrorCode.PURCHASE_REQUEST_FORMAT_INVALID;

import java.time.format.DateTimeParseException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.dto.request.PurchaseRequest;

class StringParserTest {

    private final StringParser stringParser = new StringParser();

    @ParameterizedTest
    @ValueSource(strings = {"a", "", "one"})
    @DisplayName("숫자로 변환할 수 없는 값은 입력될 수 없다")
    void t001(String input) {
        assertThatThrownBy(() -> stringParser.parseInt(input))
                .isInstanceOf(NumberFormatException.class)
                .hasMessageContaining(PURCHASE_REQUEST_FORMAT_INVALID.getMsgWithPrefix());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024년11월11일", "2024,11,11", "2024/11/11"})
    @DisplayName("날짜 형태로 변환할 수 없는 값은 입력될 수 없다")
    void t003(String input) {
        assertThatThrownBy(() -> stringParser.parseDate(input))
                .isInstanceOf(DateTimeParseException.class)
                .hasMessageContaining(CANNOT_PARSING_DATETIME.getMsgWithPrefix());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-01-01", "1999-12-16", "2024-11-11"})
    @DisplayName("문자 입력으로 주어진 날짜에 대해 LocalDate로 변환이 가능하다")
    void t004(String input) {
        assertThatCode(() -> {
            stringParser.parseDate(input);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("문자로 입력된 구매 요청 사항을 이름과 갯수로 분리 처리할 수 있다")
    void t005() {
        String testInput = "[콜라-5],[감자칩-1]";
        PurchaseRequest testRequest1 = new PurchaseRequest("콜라", 5);
        PurchaseRequest testRequest2 = new PurchaseRequest("감자칩", 1);

        List<PurchaseRequest> purchaseRequests = stringParser.parseShoppingList(testInput);

        assertThat(purchaseRequests.contains(testRequest1)).isTrue();
        assertThat(purchaseRequests.contains(testRequest2)).isTrue();
    }
}