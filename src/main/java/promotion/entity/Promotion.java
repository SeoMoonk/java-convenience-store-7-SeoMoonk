package promotion.entity;

import java.time.LocalDate;

public class Promotion {
    private String name;

    private int conditionQuantity;

    private int bonusQuantity;

    private LocalDate startDate;

    private LocalDate endDate;

    public Promotion(String name, Integer conditionQuantity, Integer bonusQuantity, LocalDate startDate,
                     LocalDate endDate) {
        this.name = name;
        this.conditionQuantity = conditionQuantity;
        this.bonusQuantity = bonusQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public int getConditionQuantity() {
        return conditionQuantity;
    }

    public int getBonusQuantity() {
        return bonusQuantity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
