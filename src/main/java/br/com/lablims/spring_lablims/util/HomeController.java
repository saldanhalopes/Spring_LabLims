package br.com.lablims.spring_lablims.util;

import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.repos.SistemaRepository;
import br.com.lablims.spring_lablims.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;


@Controller
public class HomeController {

    @Autowired
    private SistemaRepository sistemaRepository;

    @Autowired
    private final UsuarioService usuarioService;

    public HomeController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping({ "/", "/index"})
    public String goHome(final Model model, Principal principal) {
       Usuario usuario = usuarioService.findByUsername(principal.getName());
        if(usuario.getChangePass()){
            return "redirect:/alterarSenha/" + usuario.getSecret();
        }else{
            return "dashboard";
        }
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

    @GetMapping("/estoque")
    public String estoque() {
        return "estoque/index";
    }

}
