package com.gym.management.dto;

import com.gym.management.model.SubscriptionType;

import java.math.BigDecimal;

public class GymPerformanceReport {

    private String gymName;
    private String periodLabel;
    private BigDecimal totalRevenue;
    private SubscriptionType mostPopularPlan;
    private String mostPopularPlanLabel;
    private long newCustomers;
    private long returningCustomers;
    private double newCustomerPercentage;

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public SubscriptionType getMostPopularPlan() {
        return mostPopularPlan;
    }

    public void setMostPopularPlan(SubscriptionType mostPopularPlan) {
        this.mostPopularPlan = mostPopularPlan;
    }

    public String getMostPopularPlanLabel() {
        return mostPopularPlanLabel;
    }

    public void setMostPopularPlanLabel(String mostPopularPlanLabel) {
        this.mostPopularPlanLabel = mostPopularPlanLabel;
    }

    public long getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(long newCustomers) {
        this.newCustomers = newCustomers;
    }

    public long getReturningCustomers() {
        return returningCustomers;
    }

    public void setReturningCustomers(long returningCustomers) {
        this.returningCustomers = returningCustomers;
    }

    public double getNewCustomerPercentage() {
        return newCustomerPercentage;
    }

    public void setNewCustomerPercentage(double newCustomerPercentage) {
        this.newCustomerPercentage = newCustomerPercentage;
    }
}
