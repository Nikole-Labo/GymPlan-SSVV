package com.gym.management.controller;

import com.gym.management.dto.PurchaseSubscriptionForm;
import com.gym.management.exception.BusinessException;
import com.gym.management.model.SubscriptionType;
import com.gym.management.service.CustomerService;
import com.gym.management.service.GymService;
import com.gym.management.service.SubscriptionPurchaseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/purchase-subscription")
public class PurchaseSubscriptionController {

    private final SubscriptionPurchaseService subscriptionPurchaseService;
    private final CustomerService customerService;
    private final GymService gymService;

    public PurchaseSubscriptionController(
            SubscriptionPurchaseService subscriptionPurchaseService,
            CustomerService customerService,
            GymService gymService) {
        this.subscriptionPurchaseService = subscriptionPurchaseService;
        this.customerService = customerService;
        this.gymService = gymService;
    }

    @ModelAttribute("subscriptionTypes")
    public SubscriptionType[] subscriptionTypes() {
        return SubscriptionType.values();
    }

    @GetMapping
    public String form(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new PurchaseSubscriptionForm());
        }
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("gyms", gymService.findAll());
        return "purchase/form";
    }

    @PostMapping
    public String purchase(@Valid @ModelAttribute("form") PurchaseSubscriptionForm form,
                           BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("gyms", gymService.findAll());
            return "purchase/form";
        }

        try {
            subscriptionPurchaseService.purchase(form.getCustomerId(), form.getGymId(), form.getType());
            redirect.addFlashAttribute("successMessage",
                    "Subscription purchased successfully! The customer is now linked to the gym.");
            return "redirect:/abonaments";
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("gyms", gymService.findAll());
            return "purchase/form";
        }
    }
}
