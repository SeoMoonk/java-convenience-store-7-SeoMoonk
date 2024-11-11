package store.view;

import camp.nextstep.edu.missionutils.Console;
import promotion.dto.response.PromotionApplyResult;

public class StoreInputView {

    public String inputShoppingList() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        return Console.readLine();
    }

    public static String inputAnswerAboutPromotion(PromotionApplyResult result) {
        System.out.println(result.state().getFormattedMsg(result.product().getName(), result.conditionalQuantity()));
        return Console.readLine();
    }

    public static String inputAnswerAboutMembership() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
        return Console.readLine();
    }

    public static String inputAnswerAboutAdditionalPurchase() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        return Console.readLine();
    }
}
