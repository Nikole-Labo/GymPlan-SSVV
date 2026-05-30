package com.gym.management.controller;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Abonament;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.service.CustomerService;
import com.gym.management.service.GymService;
import com.gym.management.service.SubscriptionPurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PurchaseSubscriptionController.class)
class PurchaseSubscriptionControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionPurchaseService subscriptionPurchaseService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private GymService gymService;

    @Test
    void formRendersCustomerGymAndPlanControls() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(customer(1L, "Anna", "Popescu")));
        when(gymService.findAll()).thenReturn(List.of(gym(2L, "Downtown Fitness", 3)));

        mockMvc.perform(get("/purchase-subscription"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/form"))
                .andExpect(model().attributeExists("form", "customers", "gyms", "subscriptionTypes"))
                .andExpect(content().string(containsString("Purchase Subscription")))
                .andExpect(content().string(containsString("Anna Popescu")))
                .andExpect(content().string(containsString("Downtown Fitness")));
    }

    @Test
    void purchaseWithMissingRequiredSelectionsStaysOnForm() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(customer(1L, "Anna", "Popescu")));
        when(gymService.findAll()).thenReturn(List.of(gym(2L, "Downtown Fitness", 3)));

        mockMvc.perform(post("/purchase-subscription")
                        .param("type", "ONE_MONTH"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/form"))
                .andExpect(model().attributeHasFieldErrors("form", "customerId", "gymId"));

        verify(subscriptionPurchaseService, never()).purchase(any(), any(), any());
    }

    @Test
    void validPurchaseRedirectsToSubscriptionListWithSuccessMessage() throws Exception {
        when(subscriptionPurchaseService.purchase(1L, 2L, SubscriptionType.STUDENT))
                .thenReturn(new Abonament());

        mockMvc.perform(post("/purchase-subscription")
                        .param("customerId", "1")
                        .param("gymId", "2")
                        .param("type", "STUDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/abonaments"))
                .andExpect(flash().attribute("successMessage",
                        "Subscription purchased successfully! The customer is now linked to the gym."));
    }

    @Test
    void businessRuleViolationShowsErrorOnPurchaseForm() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(customer(1L, "Anna", "Popescu")));
        when(gymService.findAll()).thenReturn(List.of(gym(2L, "Downtown Fitness", 3)));
        when(subscriptionPurchaseService.purchase(1L, 2L, SubscriptionType.ONE_MONTH))
                .thenThrow(new BusinessException("Customer already has an active subscription at Downtown Fitness."));

        mockMvc.perform(post("/purchase-subscription")
                        .param("customerId", "1")
                        .param("gymId", "2")
                        .param("type", "ONE_MONTH"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/form"))
                .andExpect(model().attribute("errorMessage",
                        "Customer already has an active subscription at Downtown Fitness."))
                .andExpect(content().string(containsString("already has an active subscription")));
    }

    private static Customer customer(Long id, String firstName, String lastName) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(firstName.toLowerCase() + "@example.com");
        customer.setRegistrationDate(LocalDate.of(2026, 5, 1));
        return customer;
    }

    private static Gym gym(Long id, String name, int capacity) {
        Gym gym = new Gym();
        gym.setId(id);
        gym.setName(name);
        gym.setAddress("123 Main Street");
        gym.setCapacity(capacity);
        gym.setManagerName("Alice Johnson");
        return gym;
    }
}
