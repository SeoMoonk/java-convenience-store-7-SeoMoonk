package promotion.entity;

import java.time.LocalDate;

public class Promotion {
    private String name;

    private Integer buy;

    private Integer get;

    private LocalDate startDate;

    private LocalDate endDate;

    public Promotion(String name, Integer buy, Integer get, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public Integer getBuy() {
        return buy;
    }
}
