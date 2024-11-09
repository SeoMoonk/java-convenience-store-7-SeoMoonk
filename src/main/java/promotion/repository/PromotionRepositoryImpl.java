package promotion.repository;

import java.time.LocalDate;
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
        return storage.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst();
    }

    @Override
    public List<Promotion> findAllByStartDateBeforeAndEndDateAfter(LocalDate now) {
        return storage.stream()
                .filter(p -> !now.isBefore(p.getStartDate()))
                .filter(p -> !now.isAfter(p.getEndDate()))
                .toList();
    }
}
