package product.entity;

import static product.constants.ProductStatic.EMPTY_STATE;
import static product.constants.ProductStatic.MONETARY_UNIT;
import static product.constants.ProductStatic.PRODUCT_UNIT;

import promotion.entity.Promotion;

public class Product {

    private String name;

    private int price;

    private int quantity;

    private Promotion promotion;

    public Product(String name, int price, int quantity, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public String getFormattedPrice() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%,d", price)).append(MONETARY_UNIT);
        return sb.toString();
    }

    public String getFormattedQuantity() {
        if (this.quantity == 0) {
            return EMPTY_STATE;
        }
        return quantity + PRODUCT_UNIT;
    }

    public String getFormattedPromotionName() {
        if (promotion == null) {
            return "";
        }
        return promotion.getName();
    }

    public void subtractQuantity(int subtractQuantity) {
        this.quantity -= subtractQuantity;
    }
}
