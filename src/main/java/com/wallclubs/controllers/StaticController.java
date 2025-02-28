package com.wallclubs.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Wallclubs - About Us");
        model.addAttribute("description", "Learn about Wallclubs—your daily hub for smartphone hacks and tech news.");
        model.addAttribute("canonical", "https://wallclubs.in/about");
        model.addAttribute("pageType", "about");
        return "about";
    }
    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("title", "Wallclubs - Terms and Conditions");
        model.addAttribute("description", "Learn about Wallclubs Terms and Conditions");
        model.addAttribute("canonical", "https://wallclubs.in/terms");
        model.addAttribute("pageType", "terms");
        return "terms";
    }
}