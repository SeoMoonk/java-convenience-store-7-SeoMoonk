package promotion.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import promotion.entity.Promotion;
import promotion.repository.PromotionRepository;
import promotion.repository.PromotionRepositoryImpl;

public class PromotionService {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();

    public void setUp(String promotionFilePath) {
        try {
            File file = new File(promotionFilePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while ((line = br.readLine())!=null) {
                String[] splitPromotion = line.split(",");
                create(Arrays.asList(splitPromotion));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create(List<String> values) {
        Promotion promotion = new Promotion(values.get(0),
                Integer.parseInt(values.get(1)),
                Integer.parseInt(values.get(2)),
                LocalDate.parse(values.get(3)),
                LocalDate.parse(values.get(4)));

        promotionRepository.save(promotion);
    }

    public Optional<Promotion> getByName(String name) {
        return promotionRepository.findByName(name);
    }
}
