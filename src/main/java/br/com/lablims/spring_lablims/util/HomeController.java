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

    @GetMapping("/logs")
    public String logs() {
        return "logs/index";
    }

    @GetMapping("/laboratorio")
    public String laboratorio() {
        return "laboratorio/index";
    }
}
