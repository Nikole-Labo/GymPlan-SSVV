package com.gym.management.service;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Abonament;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.repository.AbonamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionPurchaseServiceTest {

    @Mock
    private AbonamentRepository abonamentRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private GymService gymService;

    @InjectMocks
    private SubscriptionPurchaseService service;

    @Test
    void purchaseCreatesSubscriptionWhenCustomerHasCapacityAtGym() {
        Customer customer = customer(1L);
        Gym gym = gym(2L, 3);
        LocalDate beforePurchase = LocalDate.now();

        when(customerService.findById(1L)).thenReturn(customer);
        when(gymService.findById(2L)).thenReturn(gym);
        when(abonamentRepository.existsActiveForCustomerAndGym(eq(1L), eq(2L), any(LocalDate.class)))
                .thenReturn(false);
        when(abonamentRepository.countActiveByGymId(eq(2L), any(LocalDate.class))).thenReturn(1L);
        when(abonamentRepository.save(any(Abonament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Abonament result = service.purchase(1L, 2L, SubscriptionType.STUDENT);

        assertThat(result.getCustomer()).isSameAs(customer);
        assertThat(result.getGym()).isSameAs(gym);
        assertThat(result.getType()).isEqualTo(SubscriptionType.STUDENT);
        assertThat(result.getPrice()).isEqualByComparingTo(SubscriptionType.STUDENT.getDefaultPrice());
        assertThat(result.getPurchaseDate()).isBetween(beforePurchase, LocalDate.now());
        assertThat(result.getExpirationDate())
                .isEqualTo(result.getPurchaseDate().plusMonths(1).minusDays(1));

        ArgumentCaptor<Abonament> savedSubscription = ArgumentCaptor.forClass(Abonament.class);
        verify(abonamentRepository).save(savedSubscription.capture());
        assertThat(savedSubscription.getValue()).isSameAs(result);
    }

    @Test
    void purchaseRejectsDuplicateActiveSubscriptionForSameCustomerAndGym() {
        Customer customer = customer(1L);
        Gym gym = gym(2L, 3);

        when(customerService.findById(1L)).thenReturn(customer);
        when(gymService.findById(2L)).thenReturn(gym);
        when(abonamentRepository.existsActiveForCustomerAndGym(eq(1L), eq(2L), any(LocalDate.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> service.purchase(1L, 2L, SubscriptionType.ONE_MONTH))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already has an active subscription");

        verify(abonamentRepository, never()).countActiveByGymId(eq(2L), any(LocalDate.class));
        verify(abonamentRepository, never()).save(any(Abonament.class));
    }

    @Test
    void purchaseRejectsWhenGymCapacityIsReached() {
        Customer customer = customer(1L);
        Gym gym = gym(2L, 2);

        when(customerService.findById(1L)).thenReturn(customer);
        when(gymService.findById(2L)).thenReturn(gym);
        when(abonamentRepository.existsActiveForCustomerAndGym(eq(1L), eq(2L), any(LocalDate.class)))
                .thenReturn(false);
        when(abonamentRepository.countActiveByGymId(eq(2L), any(LocalDate.class))).thenReturn(2L);

        assertThatThrownBy(() -> service.purchase(1L, 2L, SubscriptionType.SIX_MONTHS))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("has reached its maximum capacity (2)");

        verify(abonamentRepository, never()).save(any(Abonament.class));
    }

    @Test
    void calculateExpirationDateUsesInclusiveSubscriptionPeriod() {
        LocalDate purchaseDate = LocalDate.of(2026, 5, 15);

        assertThat(service.calculateExpirationDate(purchaseDate, SubscriptionType.ONE_MONTH))
                .isEqualTo(LocalDate.of(2026, 6, 14));
        assertThat(service.calculateExpirationDate(purchaseDate, SubscriptionType.SIX_MONTHS))
                .isEqualTo(LocalDate.of(2026, 11, 14));
    }

    private static Customer customer(Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName("Ana");
        customer.setLastName("Test");
        customer.setEmail("ana.test@example.com");
        customer.setRegistrationDate(LocalDate.of(2026, 5, 1));
        return customer;
    }

    private static Gym gym(Long id, int capacity) {
        Gym gym = new Gym();
        gym.setId(id);
        gym.setName("Downtown Fitness");
        gym.setAddress("123 Main Street");
        gym.setCapacity(capacity);
        gym.setManagerName("Alice Johnson");
        return gym;
    }
}
