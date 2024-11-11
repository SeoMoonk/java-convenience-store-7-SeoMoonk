package store.view;

import static store.constants.StoreInfoMsg.SUGGEST_ADDITIONAL_PURCHASE;

import camp.nextstep.edu.missionutils.Console;
import promotion.dto.response.PromotionApplyResult;
import store.constants.StoreInfoMsg;

public class StoreInputView {

    public String inputShoppingList() {
        System.out.println(StoreInfoMsg.INPUT_PRODUCT_NAME_AND_QUANTITY.getMsg());
        return Console.readLine();
    }

    public String inputAnswerAboutPromotion(PromotionApplyResult result) {
        System.out.println(result.state().getFormattedMsg(result.product().getName(), result.conditionalQuantity()));
        return Console.readLine();
    }

    public String inputAnswerAboutMembership() {
        System.out.println(StoreInfoMsg.SUGGEST_MEMBERSHIP_DISCOUNT.getMsg());
        return Console.readLine();
    }

    public String inputAnswerAboutAdditionalPurchase() {
        System.out.println(SUGGEST_ADDITIONAL_PURCHASE);
        return Console.readLine();
    }
}
