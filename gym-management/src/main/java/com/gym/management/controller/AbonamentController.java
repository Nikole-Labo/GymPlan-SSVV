package com.gym.management.controller;

import com.gym.management.model.Abonament;
import com.gym.management.model.Customer;
import com.gym.management.model.Gym;
import com.gym.management.model.SubscriptionType;
import com.gym.management.service.AbonamentService;
import com.gym.management.service.CustomerService;
import com.gym.management.service.GymService;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/abonaments")
public class AbonamentController {

    private final AbonamentService abonamentService;
    private final CustomerService customerService;
    private final GymService gymService;

    public AbonamentController(
            AbonamentService abonamentService,
            CustomerService customerService,
            GymService gymService) {
        this.abonamentService = abonamentService;
        this.customerService = customerService;
        this.gymService = gymService;
    }

    @ModelAttribute("subscriptionTypes")
    public SubscriptionType[] subscriptionTypes() {
        return SubscriptionType.values();
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("abonaments", abonamentService.findAll());
        return "abonaments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("abonament", newAbonamentForm());
        model.addAttribute("formTitle", "Add Subscription");
        addFormOptions(model);
        return "abonaments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("abonament") Abonament abonament,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        ensureRelationStubs(abonament);
        validateRelations(abonament, result);
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Add Subscription");
            addFormOptions(model);
            return "abonaments/form";
        }
        abonamentService.save(abonament);
        redirect.addFlashAttribute("successMessage", "Subscription created successfully.");
        return "redirect:/abonaments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("abonament", abonamentService.findById(id));
        model.addAttribute("formTitle", "Edit Subscription");
        addFormOptions(model);
        return "abonaments/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("abonament") Abonament abonament,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        abonament.setId(id);
        ensureRelationStubs(abonament);
        validateRelations(abonament, result);
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Edit Subscription");
            addFormOptions(model);
            return "abonaments/form";
        }
        abonamentService.save(abonament);
        redirect.addFlashAttribute("successMessage", "Subscription updated successfully.");
        return "redirect:/abonaments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        abonamentService.delete(id);
        redirect.addFlashAttribute("successMessage", "Subscription deleted successfully.");
        return "redirect:/abonaments";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("gyms", gymService.findAll());
    }

    private Abonament newAbonamentForm() {
        Abonament abonament = new Abonament();
        abonament.setCustomer(new Customer());
        abonament.setGym(new Gym());
        abonament.setPurchaseDate(LocalDate.now());
        abonament.setType(SubscriptionType.ONE_MONTH);
        abonament.setPrice(SubscriptionType.ONE_MONTH.getDefaultPrice());
        abonament.setExpirationDate(
                abonamentService.defaultExpirationDate(LocalDate.now(), SubscriptionType.ONE_MONTH));
        return abonament;
    }

    private void ensureRelationStubs(Abonament abonament) {
        if (abonament.getCustomer() == null) {
            abonament.setCustomer(new Customer());
        }
        if (abonament.getGym() == null) {
            abonament.setGym(new Gym());
        }
    }

    private void validateRelations(Abonament abonament, BindingResult result) {
        if (abonament.getCustomer().getId() == null) {
            result.rejectValue("customer.id", "customer.required", "Please select a customer.");
        }
        if (abonament.getGym().getId() == null) {
            result.rejectValue("gym.id", "gym.required", "Please select a gym.");
        }
    }
}
