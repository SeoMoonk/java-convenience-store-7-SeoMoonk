package product.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import product.entity.Product;
import promotion.entity.Promotion;

public class ProductRepositoryImpl implements ProductRepository {

    private List<Product> storage = new ArrayList<>();

    @Override
    public void removeAll() {
        storage.clear();
    }

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

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<Product> findAllByName(String name) {
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .toList();
    }

    @Override
    public int countAllByName(String name) {
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .mapToInt(Product::getQuantity)
                .sum();
    }

    @Override
    public Optional<Product> findByNameAndPromotion(String name, Promotion promotion) {
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .filter(p -> promotion.equals(p.getPromotion()))
                .findFirst();
    }

    @Override
    public Optional<Product> findByNameAndHasPromotion(String name) {
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .filter(p -> Optional.ofNullable(p.getPromotion()).isPresent()) // Optional 사용
                .findFirst();
    }

    @Override
    public Optional<Product> findByNameAndNotHasPromotion(String name) {
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .filter(p -> Optional.ofNullable(p.getPromotion()).isEmpty())
                .findFirst();
    }

    @Override
    public List<String> findAllNames() {
        return storage.stream()
                .map(Product::getName)
                .collect(Collectors.toList());
    }
}
