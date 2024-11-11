package product.dto;

import product.entity.Product;

public record ProductInfo(
        String name,
        String price,
        String quantity,
        String promotionName
) {
    public static ProductInfo fromProduct(Product product) {
        return new ProductInfo(product.getName(),
                product.getFormattedPrice(),
                product.getFormattedQuantity(),
                product.getFormattedPromotionName());
    }
}

