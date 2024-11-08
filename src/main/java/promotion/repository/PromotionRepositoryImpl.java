package promotion.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import promotion.entity.Promotion;

public class PromotionRepositoryImpl implements PromotionRepository {

    private List<Promotion> storage = new ArrayList<>();

    @Override
    public void save(Promotion promotion) {
        storage.add(promotion);
    }

    @Override
    public Optional<Promotion> findByName(String name) {
        for (Promotion p : storage) {
            if (p.getName().equals(name)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
}
