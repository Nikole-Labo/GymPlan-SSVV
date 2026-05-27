package com.gym.management.controller;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Gym;
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

@Controller
@RequestMapping("/gyms")
public class GymController {

    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("gyms", gymService.findAll());
        return "gyms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("gym", new Gym());
        model.addAttribute("formTitle", "Add Gym");
        return "gyms/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("gym") Gym gym, BindingResult result, Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Add Gym");
            return "gyms/form";
        }
        gymService.save(gym);
        redirect.addFlashAttribute("successMessage", "Gym created successfully.");
        return "redirect:/gyms";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("gym", gymService.findById(id));
        model.addAttribute("formTitle", "Edit Gym");
        return "gyms/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("gym") Gym gym,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Edit Gym");
            return "gyms/form";
        }
        gym.setId(id);
        gymService.save(gym);
        redirect.addFlashAttribute("successMessage", "Gym updated successfully.");
        return "redirect:/gyms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            gymService.delete(id);
            redirect.addFlashAttribute("successMessage", "Gym deleted successfully.");
        } catch (BusinessException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/gyms";
    }
}
