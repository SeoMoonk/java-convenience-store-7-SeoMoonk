package store.view;

import java.util.List;
import product.dto.response.ProductInfo;
import store.dto.response.FinalBonus;
import store.dto.response.FinalPurchase;
import store.dto.response.ReceiptItems;
import store.dto.response.ReceiptPriceInfo;

public class StoreOutputView {

    public void printStartMsg() {
        System.out.println("안녕하세요. W편의점입니다.");
    }

    public void printProductInfos(List<ProductInfo> productInfos) {
        StringBuilder sb = new StringBuilder();
        sb.append("현재 보유하고 있는 상품입니다.\n\n");
        for (ProductInfo info : productInfos) {
            sb.append("- " + info.name() + " " + info.price() + " "
                    + info.quantity() + " " + info.promotionName() + "\n");
        }
        System.out.println(sb);
    }

    public void printReceipt(ReceiptItems receiptItems, ReceiptPriceInfo receiptPriceInfo) {
        StringBuilder sb = new StringBuilder();
        sb = printPurchaseItems(receiptItems.purchases(), sb);
        sb = printBonusItems(receiptItems.bonuses(), sb);
        sb = printPriceInfo(receiptPriceInfo, sb);
        System.out.println(sb);
    }

    private StringBuilder printPurchaseItems(List<FinalPurchase> purchaseItems, StringBuilder sb) {
        if (!purchaseItems.isEmpty()) {
            sb.append("==============W 편의점================\n");
            sb.append("%-10s\t%-5s\t%-8s\n".formatted("상품명", "수량",  "금액"));
            for(FinalPurchase item : purchaseItems) {
                sb.append("%-10s\t%-5d\t%,8d\n".formatted(item.productName(), item.quantity(), item.price()));
            }
        }
        return sb;
    }

    private StringBuilder printBonusItems(List<FinalBonus> bonusItems, StringBuilder sb) {
        if (!bonusItems.isEmpty()) {
            sb.append("==============증\t정================\n");
            sb.append("상품명\t\t수량\t\t금액\t\t\n");
            for(FinalBonus item : bonusItems) {
                sb.append("%-10s\t%d\n".formatted(item.productName(), item.bonusQuantity()));
            }
        }
        return sb;
    }

    private StringBuilder printPriceInfo(ReceiptPriceInfo info, StringBuilder sb) {
        sb.append("====================================\n");
        sb.append("%-10s\t%-5d\t%,8d\n".formatted("총구매액",info.totalQuantity(), info.totalAmount()));
        sb.append("%-10s\t\t\t%,8d\n".formatted("행사할인", info.promotionDiscountAmount() * -1));
        sb.append("%-10s\t\t\t\t%,8d\n".formatted("멤버십할인", info.memberShipDiscountAmount() * -1));
        sb.append("%-10s\t\t\t%,8d\n".formatted("내실돈", info.requiredAmount()));
        return sb;
    }
}
