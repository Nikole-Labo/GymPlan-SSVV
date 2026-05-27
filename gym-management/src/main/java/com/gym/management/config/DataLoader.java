package com.gym.management.config;

import com.gym.management.model.Abonament;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.repository.AbonamentRepository;
import com.gym.management.repository.CustomerRepository;
import com.gym.management.repository.GymRepository;
import com.gym.management.service.SubscriptionPurchaseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class DataLoader implements CommandLineRunner {

    private final GymRepository gymRepository;
    private final CustomerRepository customerRepository;
    private final AbonamentRepository abonamentRepository;
    private final SubscriptionPurchaseService subscriptionPurchaseService;

    public DataLoader(
            GymRepository gymRepository,
            CustomerRepository customerRepository,
            AbonamentRepository abonamentRepository,
            SubscriptionPurchaseService subscriptionPurchaseService) {
        this.gymRepository = gymRepository;
        this.customerRepository = customerRepository;
        this.abonamentRepository = abonamentRepository;
        this.subscriptionPurchaseService = subscriptionPurchaseService;
    }

    @Override
    public void run(String... args) {
        if (gymRepository.count() > 0) {
            return;
        }

        Gym downtown = new Gym();
        downtown.setName("Downtown Fitness");
        downtown.setAddress("123 Main Street");
        downtown.setCapacity(3);
        downtown.setManagerName("Alice Johnson");
        downtown = gymRepository.save(downtown);

        Gym westside = new Gym();
        westside.setName("Westside Gym");
        westside.setAddress("45 Oak Avenue");
        westside.setCapacity(10);
        westside.setManagerName("Bob Smith");
        westside = gymRepository.save(westside);

        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(5);

        Customer anna = createCustomer("Anna", "Popescu", "anna@example.com", monthStart);
        Customer ion = createCustomer("Ion", "Ionescu", "ion@example.com", monthStart.minusMonths(2));
        Customer maria = createCustomer("Maria", "Dumitru", "maria@example.com", monthStart);
        Customer andrei = createCustomer("Andrei", "Vasile", "andrei@example.com", currentMonth.minusMonths(1).atDay(10));
        Customer elena = createCustomer("Elena", "Marin", "elena@example.com", monthStart.minusMonths(6));

        subscriptionPurchaseService.purchase(anna.getId(), downtown.getId(), SubscriptionType.ONE_MONTH);
        subscriptionPurchaseService.purchase(ion.getId(), downtown.getId(), SubscriptionType.SIX_MONTHS);
        subscriptionPurchaseService.purchase(maria.getId(), downtown.getId(), SubscriptionType.STUDENT);

        subscriptionPurchaseService.purchase(andrei.getId(), westside.getId(), SubscriptionType.ONE_MONTH);
        subscriptionPurchaseService.purchase(elena.getId(), westside.getId(), SubscriptionType.SIX_MONTHS);

        Abonament expired = new Abonament();
        expired.setCustomer(ion);
        expired.setGym(downtown);
        expired.setType(SubscriptionType.ONE_MONTH);
        expired.setPrice(SubscriptionType.ONE_MONTH.getDefaultPrice());
        expired.setPurchaseDate(currentMonth.minusMonths(2).atDay(1));
        expired.setExpirationDate(currentMonth.minusMonths(1).atEndOfMonth());
        abonamentRepository.save(expired);
    }

    private Customer createCustomer(String firstName, String lastName, String email, LocalDate registrationDate) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setRegistrationDate(registrationDate);
        return customerRepository.save(customer);
    }
}
