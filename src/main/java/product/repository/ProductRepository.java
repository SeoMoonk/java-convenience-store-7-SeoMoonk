package product.repository;

import java.util.List;
import java.util.Optional;
import product.entity.Product;
import promotion.entity.Promotion;

public interface ProductRepository {
    void save(Product product);
    Optional<Product> findByName(String name);
    List<Product> findAll();
    List<Product> findAllByName(String name);
    int countAllByName(String name);
    Optional<Product> findByNameAndPromotion(String name, Promotion promotion);
    Optional<Product> findByNameAndHasPromotion(String name);
    Optional<Product> findByNameAndNotHasPromotion(String name);
}
