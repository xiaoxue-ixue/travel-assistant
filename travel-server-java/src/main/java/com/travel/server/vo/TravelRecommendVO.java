package com.travel.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelRecommendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String city;
    private Integer days;
    private Integer totalBudget;
    private List<DailyItinerary> dailyItinerary;
    private BudgetBreakdown budgetBreakdown;
    private List<String> tips;
    private List<String> warnings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyItinerary implements Serializable {
        private Integer day;
        private String date;
        private ItineraryItem morning;
        private ItineraryItem afternoon;
        private ItineraryItem evening;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItineraryItem implements Serializable {
        private String spot;
        private String duration;
        private String ticket;
        private String transportation;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetBreakdown implements Serializable {
        private Integer accommodation;
        private Integer food;
        private Integer transportation;
        private Integer tickets;
        private Integer other;
    }
}
