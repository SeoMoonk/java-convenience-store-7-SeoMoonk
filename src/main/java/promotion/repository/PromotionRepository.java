package promotion.repository;

import java.util.Optional;
import promotion.entity.Promotion;

public interface PromotionRepository {

    void save(Promotion promotion);

    Optional<Promotion> findByName(String name);
}
