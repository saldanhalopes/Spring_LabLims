package br.com.lablims.spring_lablims.controller.auth;

import br.com.lablims.spring_lablims.model.AuthenticationRequest;
import br.com.lablims.spring_lablims.model.UsuarioDTO;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) final Boolean loginRequired,
            @RequestParam(required = false) final Boolean loginError,
            @RequestParam(required = false) final Boolean logoutSuccess,
            @RequestParam(required = false) final Boolean invalidSession,
                        final Model model) {
        // dummy for using the inputRow fragment
        model.addAttribute("authentication", new AuthenticationRequest());
        if (loginRequired == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("authentication.login.required"));
        }
        if (loginError == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
        }
        if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_WARNING, WebUtils.getMessage("authentication.logout.success"));
        }
        if (invalidSession == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_WARNING, WebUtils.getMessage("authentication.invalid.session"));
        }
        return "authentication/login";
    }




}
