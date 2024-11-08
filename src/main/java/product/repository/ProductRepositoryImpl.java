package product.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import product.entity.Product;

public class ProductRepositoryImpl implements ProductRepository {

    private List<Product> storage = new ArrayList<>();

    @Override
    public void save(Product product) {
        storage.add(product);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst();
    }
}
