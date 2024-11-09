package promotion.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import promotion.entity.Promotion;

public interface PromotionRepository {

    void save(Promotion promotion);

    Optional<Promotion> findByName(String name);

    List<Promotion> findAllByStartDateBeforeAndEndDateAfter(LocalDate now);
}
