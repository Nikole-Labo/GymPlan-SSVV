package com.gym.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ReportFilterForm {

    @NotNull(message = "Please select a gym")
    private Long gymId;

    @NotBlank(message = "Please select a month")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Please enter a valid month")
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
