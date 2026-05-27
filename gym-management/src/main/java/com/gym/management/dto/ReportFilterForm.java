package com.gym.management.dto;

import jakarta.validation.constraints.NotNull;

public class ReportFilterForm {

    @NotNull(message = "Please select a gym")
    private Long gymId;

    @NotNull(message = "Please select a month")
    private String yearMonth;

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
}
