package com.gym.management.service;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Abonament;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.repository.AbonamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class SubscriptionPurchaseService {

    private final AbonamentRepository abonamentRepository;
    private final CustomerService customerService;
    private final GymService gymService;

    public SubscriptionPurchaseService(
            AbonamentRepository abonamentRepository,
            CustomerService customerService,
            GymService gymService) {
        this.abonamentRepository = abonamentRepository;
        this.customerService = customerService;
        this.gymService = gymService;
    }

    public Abonament purchase(Long customerId, Long gymId, SubscriptionType type) {
        Customer customer = customerService.findById(customerId);
        Gym gym = gymService.findById(gymId);
        LocalDate today = LocalDate.now();

        if (abonamentRepository.existsActiveForCustomerAndGym(customerId, gymId, today)) {
            throw new BusinessException(
                    "Customer already has an active subscription at " + gym.getName() + ".");
        }

        long activeCount = abonamentRepository.countActiveByGymId(gymId, today);
        if (activeCount >= gym.getCapacity()) {
            throw new BusinessException(
                    "Gym \"" + gym.getName() + "\" has reached its maximum capacity (" + gym.getCapacity() + ").");
        }

        LocalDate purchaseDate = today;
        Abonament abonament = new Abonament();
        abonament.setCustomer(customer);
        abonament.setGym(gym);
        abonament.setType(type);
        abonament.setPrice(type.getDefaultPrice());
        abonament.setPurchaseDate(purchaseDate);
        abonament.setExpirationDate(calculateExpirationDate(purchaseDate, type));

        return abonamentRepository.save(abonament);
    }

    public LocalDate calculateExpirationDate(LocalDate purchaseDate, SubscriptionType type) {
        return purchaseDate.plusMonths(type.getDurationMonths()).minusDays(1);
    }
}
