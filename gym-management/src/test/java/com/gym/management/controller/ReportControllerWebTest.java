package com.gym.management.controller;

import com.gym.management.dto.GymPerformanceReport;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.service.GymReportService;
import com.gym.management.service.GymService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ReportController.class)
class ReportControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymReportService gymReportService;

    @MockBean
    private GymService gymService;

    @Test
    void reportFormRendersWithAvailableGyms() throws Exception {
        when(gymService.findAll()).thenReturn(List.of(gym(1L, "Downtown Fitness")));

        mockMvc.perform(get("/reports/gym-performance"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/gym-performance"))
                .andExpect(model().attributeExists("filter", "gyms"))
                .andExpect(content().string(containsString("Gym Performance Dashboard")))
                .andExpect(content().string(containsString("Downtown Fitness")));
    }

    @Test
    void blankMonthShowsValidationErrorInsteadOfGeneratingReport() throws Exception {
        when(gymService.findAll()).thenReturn(List.of(gym(1L, "Downtown Fitness")));

        mockMvc.perform(post("/reports/gym-performance")
                        .param("gymId", "1")
                        .param("yearMonth", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/gym-performance"))
                .andExpect(model().attributeHasFieldErrors("filter", "yearMonth"));

        verify(gymReportService, never()).generatePerformanceReport(1L, YearMonth.of(2026, 5));
    }

    @Test
    void invalidMonthShowsValidationErrorInsteadOfServerError() throws Exception {
        when(gymService.findAll()).thenReturn(List.of(gym(1L, "Downtown Fitness")));

        mockMvc.perform(post("/reports/gym-performance")
                        .param("gymId", "1")
                        .param("yearMonth", "2026-13"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/gym-performance"))
                .andExpect(model().attributeHasFieldErrors("filter", "yearMonth"));

        verify(gymReportService, never()).generatePerformanceReport(1L, YearMonth.of(2026, 5));
    }

    @Test
    void validReportRequestRendersGeneratedReport() throws Exception {
        GymPerformanceReport report = new GymPerformanceReport();
        report.setGymName("Downtown Fitness");
        report.setPeriodLabel("May 2026");
        report.setTotalRevenue(new BigDecimal("329.97"));
        report.setMostPopularPlan(SubscriptionType.STUDENT);
        report.setMostPopularPlanLabel("Student");
        report.setNewCustomers(2);
        report.setReturningCustomers(1);
        report.setNewCustomerPercentage(66.7);

        when(gymService.findAll()).thenReturn(List.of(gym(1L, "Downtown Fitness")));
        when(gymReportService.generatePerformanceReport(1L, YearMonth.of(2026, 5))).thenReturn(report);

        mockMvc.perform(post("/reports/gym-performance")
                        .param("gymId", "1")
                        .param("yearMonth", "2026-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/gym-performance"))
                .andExpect(model().attribute("reportGenerated", true))
                .andExpect(model().attribute("report", report))
                .andExpect(content().string(containsString("Downtown Fitness")))
                .andExpect(content().string(containsString("Student")))
                .andExpect(content().string(containsString("66.7%")));
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
}
