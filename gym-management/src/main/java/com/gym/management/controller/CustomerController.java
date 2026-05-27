package com.gym.management.controller;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Customer;
import com.gym.management.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Customer customer = new Customer();
        customer.setRegistrationDate(java.time.LocalDate.now());
        model.addAttribute("customer", customer);
        model.addAttribute("formTitle", "Add Customer");
        return "customers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("customer") Customer customer, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Add Customer");
            return "customers/form";
        }
        try {
            customerService.save(customer);
            redirect.addFlashAttribute("successMessage", "Customer created successfully.");
            return "redirect:/customers";
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "email.duplicate", ex.getMessage());
            model.addAttribute("formTitle", "Add Customer");
            return "customers/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.findById(id));
        model.addAttribute("formTitle", "Edit Customer");
        return "customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("customer") Customer customer,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Edit Customer");
            return "customers/form";
        }
        customer.setId(id);
        try {
            customerService.save(customer);
            redirect.addFlashAttribute("successMessage", "Customer updated successfully.");
            return "redirect:/customers";
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "email.duplicate", ex.getMessage());
            model.addAttribute("formTitle", "Edit Customer");
            return "customers/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            customerService.delete(id);
            redirect.addFlashAttribute("successMessage", "Customer deleted successfully.");
        } catch (BusinessException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customers";
    }
}
