package promotion.constants;

public enum PromotionApplyState {
    FULL_PROMOTION_APPLIED(true, "모든 품목에 프로모션 적용이 가능합니다"),
    ADDITIONAL_PROMOTION_AVAILABLE(false, "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"),
    PARTIAL_PROMOTION_APPLIED(false, "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)"),
    NO_PROMOTION_AVAILABLE(false, "프로모션 적용이 불가한 요청입니다.");

    private boolean isSuccess;
    private String msg;

    PromotionApplyState(boolean isSuccess, String msg) {
        this.isSuccess = isSuccess;
        this.msg = msg;
    }

    public String getFormattedMsg(String productName, int quantity) {
        return this.msg.formatted(productName, quantity);
    }

    public boolean isSucceseState() {
        return isSuccess;
    }
}
