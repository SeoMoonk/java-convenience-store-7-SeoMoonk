package product.repository;

import java.util.List;
import java.util.Optional;
import product.entity.Product;

public interface ProductRepository {
    void save(Product product);
    Optional<Product> findByName(String name);
    List<Product> findAll();
    List<Product> findAllByName(String name);
    int countAllByName(String name);
}
