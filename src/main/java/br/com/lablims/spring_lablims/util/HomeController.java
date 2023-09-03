package br.com.lablims.spring_lablims.util;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

    @GetMapping({ "/", "/index"})
    public String goHome() {
        return "dashboard";
    }

    @GetMapping("/parameters")
    public String parameters() {
        return "parameters/index";
    }

    @RequestMapping("/contact")
    public String contact() {
        return "contact";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

}
