package com.gym.management.service;

import com.gym.management.dto.GymPerformanceReport;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.repository.AbonamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymReportServiceTest {

    @Mock
    private AbonamentRepository abonamentRepository;

    @Mock
    private GymService gymService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private GymReportService service;

    @Test
    void generatePerformanceReportCalculatesRevenuePopularityAndCustomerMix() {
        YearMonth period = YearMonth.of(2026, 5);
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        when(gymService.findById(10L)).thenReturn(gym(10L, "Downtown Fitness"));
        when(abonamentRepository.sumRevenueByGymAndPeriod(10L, start, end))
                .thenReturn(new BigDecimal("329.970"));
        when(abonamentRepository.findMostPopularTypes(10L, start, end))
                .thenReturn(List.of(SubscriptionType.STUDENT));
        when(abonamentRepository.findDistinctCustomerIdsWithPurchases(10L, start, end))
                .thenReturn(List.of(1L, 2L, 3L));
        when(customerService.findById(1L)).thenReturn(customer(1L, period.atDay(3)));
        when(customerService.findById(2L)).thenReturn(customer(2L, period.minusMonths(2).atDay(10)));
        when(customerService.findById(3L)).thenReturn(customer(3L, period.atDay(18)));

        GymPerformanceReport report = service.generatePerformanceReport(10L, period);

        assertThat(report.getGymName()).isEqualTo("Downtown Fitness");
        assertThat(report.getPeriodLabel()).isEqualTo("May 2026");
        assertThat(report.getTotalRevenue()).isEqualByComparingTo("329.97");
        assertThat(report.getMostPopularPlan()).isEqualTo(SubscriptionType.STUDENT);
        assertThat(report.getMostPopularPlanLabel()).isEqualTo("Student");
        assertThat(report.getNewCustomers()).isEqualTo(2);
        assertThat(report.getReturningCustomers()).isEqualTo(1);
        assertThat(report.getNewCustomerPercentage()).isEqualTo(66.7);
    }

    @Test
    void generatePerformanceReportHandlesMonthWithoutSubscriptions() {
        YearMonth period = YearMonth.of(2026, 6);
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        when(gymService.findById(10L)).thenReturn(gym(10L, "Downtown Fitness"));
        when(abonamentRepository.sumRevenueByGymAndPeriod(10L, start, end)).thenReturn(null);
        when(abonamentRepository.findMostPopularTypes(10L, start, end)).thenReturn(List.of());
        when(abonamentRepository.findDistinctCustomerIdsWithPurchases(10L, start, end)).thenReturn(List.of());

        GymPerformanceReport report = service.generatePerformanceReport(10L, period);

        assertThat(report.getTotalRevenue()).isEqualByComparingTo("0.00");
        assertThat(report.getMostPopularPlan()).isNull();
        assertThat(report.getMostPopularPlanLabel()).isEqualTo("No subscriptions sold");
        assertThat(report.getNewCustomers()).isZero();
        assertThat(report.getReturningCustomers()).isZero();
        assertThat(report.getNewCustomerPercentage()).isZero();
    }

    private static Gym gym(Long id, String name) {
        Gym gym = new Gym();
        gym.setId(id);
        gym.setName(name);
        gym.setAddress("123 Main Street");
        gym.setCapacity(10);
        gym.setManagerName("Alice Johnson");
        return gym;
    }

    private static Customer customer(Long id, LocalDate registrationDate) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName("Customer");
        customer.setLastName(String.valueOf(id));
        customer.setEmail("customer" + id + "@example.com");
        customer.setRegistrationDate(registrationDate);
        return customer;
    }
}
