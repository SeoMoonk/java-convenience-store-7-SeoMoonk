package store.constants;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;

public enum StoreErrorCode {
    PURCHASE_REQUEST_FORMAT_INVALID("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    NONE_EXISTENT_PRODUCT("존재하지 않는 상품입니다. 다시 입력해 주세요."),
    EXCEED_QUANTITY_IN_STORAGE("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."),
    INVALID_INPUT("잘못된 입력입니다. 다시 입력해 주세요."),
    CANNOT_ANSWER_PROCESS("응답할 수 없는 프로세스입니다."),
    CANNOT_REQUEST_OVER_STORED_QUANTITY("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");

    private String msg;

    StoreErrorCode(String msg) {
        this.msg = msg;
    }

    public String getMsgWithPrefix() {
        return ERROR_MSG_PREFIX + msg;
    }
}
