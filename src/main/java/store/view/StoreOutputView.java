package store.view;

import java.util.List;
import product.dto.response.ProductInfo;

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
}
