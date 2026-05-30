package com.gym.management.controller;

import com.gym.management.service.AbonamentService;
import com.gym.management.service.CustomerService;
import com.gym.management.service.GymService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({
        HomeController.class,
        GymController.class,
        CustomerController.class,
        AbonamentController.class
})
class NavigationWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymService gymService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AbonamentService abonamentService;

    @Test
    void homePageShowsMainNavigationTargets() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Gym Subscription Management")))
                .andExpect(content().string(containsString("Purchase Subscription")))
                .andExpect(content().string(containsString("Gym Performance Report")));
    }

    @Test
    void listPagesRenderEmptyStates() throws Exception {
        when(gymService.findAll()).thenReturn(List.of());
        when(customerService.findAll()).thenReturn(List.of());
        when(abonamentService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/gyms"))
                .andExpect(status().isOk())
                .andExpect(view().name("gyms/list"))
                .andExpect(model().attributeExists("gyms"))
                .andExpect(content().string(containsString("No gyms yet.")));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/list"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(content().string(containsString("No customers yet.")));

        mockMvc.perform(get("/abonaments"))
                .andExpect(status().isOk())
                .andExpect(view().name("abonaments/list"))
                .andExpect(model().attributeExists("abonaments"))
                .andExpect(content().string(containsString("No subscriptions yet.")));
    }

    @Test
    void createFormsRenderExpectedInputs() throws Exception {
        when(customerService.findAll()).thenReturn(List.of());
        when(gymService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/gyms/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("gyms/form"))
                .andExpect(content().string(containsString("Capacity (max active subscriptions)")));

        mockMvc.perform(get("/customers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/form"))
                .andExpect(content().string(containsString("Registration Date")));

        mockMvc.perform(get("/abonaments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("abonaments/form"))
                .andExpect(content().string(containsString("For purchases with validations")));
    }
}
