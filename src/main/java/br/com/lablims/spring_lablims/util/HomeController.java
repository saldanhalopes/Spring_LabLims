package br.com.lablims.spring_lablims.util;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String goHome() {
        return "/index";
    }

    @GetMapping("/index")
    public String index() {
        return "/index";
    }

    @GetMapping("/parameters")
    public String parameters() {
        return "/parameters/index";
    }

}
