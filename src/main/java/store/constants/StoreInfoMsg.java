package store.constants;

public enum StoreInfoMsg {
    INPUT_PRODUCT_NAME_AND_QUANTITY("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])"),
    SUGGEST_MEMBERSHIP_DISCOUNT("멤버십 할인을 받으시겠습니까? (Y/N)"),
    SUGGEST_ADDITIONAL_PURCHASE("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"),

    START_MSG_FOR_VISIT("안녕하세요. W편의점입니다."),
    START_MSG_FOR_PRINT_PRODUCTS_LIST("현재 보유하고 있는 상품입니다.\n\n"),
    START_MSG_FOR_PRINT_RECEIPT("==============W 편의점================\n"),
    START_MSG_FOR_PRINT_PROMOTIONS("==============증\t정================\n"),
    PROMOTION_LIST_PRINT_START_FORM("상품명\t\t수량\t\t금액\t\t");

    private String msg;

    StoreInfoMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
