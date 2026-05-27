package com.gym.management.service;

import com.gym.management.dto.GymPerformanceReport;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.repository.AbonamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class GymReportService {

    private static final DateTimeFormatter PERIOD_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private final AbonamentRepository abonamentRepository;
    private final GymService gymService;
    private final CustomerService customerService;

    public GymReportService(
            AbonamentRepository abonamentRepository,
            GymService gymService,
            CustomerService customerService) {
        this.abonamentRepository = abonamentRepository;
        this.gymService = gymService;
        this.customerService = customerService;
    }

    public GymPerformanceReport generatePerformanceReport(Long gymId, YearMonth yearMonth) {
        Gym gym = gymService.findById(gymId);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal totalRevenue = abonamentRepository.sumRevenueByGymAndPeriod(gymId, start, end);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        List<SubscriptionType> popularTypes =
                abonamentRepository.findMostPopularTypes(gymId, start, end);
        SubscriptionType mostPopular = popularTypes.isEmpty() ? null : popularTypes.get(0);

        List<Long> customerIds =
                abonamentRepository.findDistinctCustomerIdsWithPurchases(gymId, start, end);

        long newCustomers = 0;
        long returningCustomers = 0;
        for (Long customerId : customerIds) {
            Customer customer = customerService.findById(customerId);
            YearMonth registrationMonth = YearMonth.from(customer.getRegistrationDate());
            if (registrationMonth.equals(yearMonth)) {
                newCustomers++;
            } else {
                returningCustomers++;
            }
        }

        long totalBuyers = newCustomers + returningCustomers;
        double newPercentage = totalBuyers == 0
                ? 0.0
                : (newCustomers * 100.0) / totalBuyers;

        GymPerformanceReport report = new GymPerformanceReport();
        report.setGymName(gym.getName());
        report.setPeriodLabel(yearMonth.format(PERIOD_FORMAT));
        report.setTotalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP));
        report.setMostPopularPlan(mostPopular);
        report.setMostPopularPlanLabel(
                mostPopular == null ? "No subscriptions sold" : mostPopular.getLabel());
        report.setNewCustomers(newCustomers);
        report.setReturningCustomers(returningCustomers);
        report.setNewCustomerPercentage(Math.round(newPercentage * 10.0) / 10.0);
        return report;
    }
}
