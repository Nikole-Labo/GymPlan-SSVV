package com.gym.management.controller;

import com.gym.management.dto.GymPerformanceReport;
import com.gym.management.dto.ReportFilterForm;
import com.gym.management.service.GymReportService;
import com.gym.management.service.GymService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.YearMonth;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final GymReportService gymReportService;
    private final GymService gymService;

    public ReportController(GymReportService gymReportService, GymService gymService) {
        this.gymReportService = gymReportService;
        this.gymService = gymService;
    }

    @GetMapping("/gym-performance")
    public String gymPerformanceForm(Model model) {
        ReportFilterForm form = new ReportFilterForm();
        form.setYearMonth(YearMonth.now().toString());
        model.addAttribute("filter", form);
        model.addAttribute("gyms", gymService.findAll());
        return "reports/gym-performance";
    }

    @PostMapping("/gym-performance")
    public String generateReport(@Valid @ModelAttribute("filter") ReportFilterForm filter,
                                 BindingResult result, Model model) {
        model.addAttribute("gyms", gymService.findAll());
        if (result.hasErrors()) {
            return "reports/gym-performance";
        }

        YearMonth yearMonth = YearMonth.parse(filter.getYearMonth());
        GymPerformanceReport report =
                gymReportService.generatePerformanceReport(filter.getGymId(), yearMonth);
        model.addAttribute("report", report);
        model.addAttribute("reportGenerated", true);
        return "reports/gym-performance";
    }
}
