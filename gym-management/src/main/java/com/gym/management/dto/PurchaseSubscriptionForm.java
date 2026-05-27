package com.gym.management.dto;

import com.gym.management.model.SubscriptionType;
import jakarta.validation.constraints.NotNull;

public class PurchaseSubscriptionForm {

    @NotNull(message = "Please select a customer")
    private Long customerId;

    @NotNull(message = "Please select a gym")
    private Long gymId;

    @NotNull(message = "Please select a subscription type")
    private SubscriptionType type;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }
}
