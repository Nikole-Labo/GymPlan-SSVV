package com.gym.management.service;

import com.gym.management.model.Abonament;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.repository.AbonamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AbonamentService {

    private final AbonamentRepository abonamentRepository;
    private final CustomerService customerService;
    private final GymService gymService;
    private final SubscriptionPurchaseService subscriptionPurchaseService;

    public AbonamentService(
            AbonamentRepository abonamentRepository,
            CustomerService customerService,
            GymService gymService,
            SubscriptionPurchaseService subscriptionPurchaseService) {
        this.abonamentRepository = abonamentRepository;
        this.customerService = customerService;
        this.gymService = gymService;
        this.subscriptionPurchaseService = subscriptionPurchaseService;
    }

    @Transactional(readOnly = true)
    public List<Abonament> findAll() {
        return abonamentRepository.findAllWithCustomerAndGym();
    }

    @Transactional(readOnly = true)
    public Abonament findById(Long id) {
        return abonamentRepository.findByIdWithCustomerAndGym(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));
    }

    public Abonament save(Abonament abonament) {
        Customer customer = customerService.findById(abonament.getCustomer().getId());
        Gym gym = gymService.findById(abonament.getGym().getId());
        abonament.setCustomer(customer);
        abonament.setGym(gym);

        if (abonament.getType() != null && abonament.getPurchaseDate() != null
                && abonament.getExpirationDate() == null) {
            abonament.setExpirationDate(
                    subscriptionPurchaseService.calculateExpirationDate(
                            abonament.getPurchaseDate(), abonament.getType()));
        }

        if (abonament.getPrice() == null && abonament.getType() != null) {
            abonament.setPrice(abonament.getType().getDefaultPrice());
        }

        return abonamentRepository.save(abonament);
    }

    public void delete(Long id) {
        if (!abonamentRepository.existsById(id)) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }
        abonamentRepository.deleteById(id);
    }

    public LocalDate defaultExpirationDate(LocalDate purchaseDate, SubscriptionType type) {
        return subscriptionPurchaseService.calculateExpirationDate(purchaseDate, type);
    }
}
