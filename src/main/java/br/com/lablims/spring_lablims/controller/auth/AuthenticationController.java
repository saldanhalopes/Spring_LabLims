package br.com.lablims.spring_lablims.controller.auth;

import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import br.com.lablims.spring_lablims.model.AuthenticationDTO;
import br.com.lablims.spring_lablims.repos.SegurancaRepository;
import br.com.lablims.spring_lablims.repos.SistemaRepository;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.QRCodeService;
import br.com.lablims.spring_lablims.util.WebUtils;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Base64;


@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private SistemaRepository sistemaRepository;
    @Autowired
    private SegurancaRepository segurancaRepository;

    private final UsuarioService usuarioService;


    @GetMapping("/login")
    public String login(@RequestParam(required = false) final Boolean loginRequired,
                        @RequestParam(required = false) final Boolean loginError,
                        @RequestParam(required = false) final Boolean logoutSuccess,
                        @RequestParam(required = false) final Boolean invalidSession,
                        final Model model) throws BadCredentialsException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        // dummy for using the inputRow fragment
        model.addAttribute("authentication", new AuthenticationDTO());
        model.addAttribute("user2FaCode", segurancaRepository.findBySegurancaTipo(SegurancaTipo.user2faCode).getValue().equalsIgnoreCase("1"));
        if (loginRequired == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("authentication.login.required"));
        }
        if (loginError == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_WARNING, WebUtils.getMessage("authentication.logout.success"));
        }
        if (invalidSession == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_WARNING, WebUtils.getMessage("authentication.invalid.session"));
        }
        return "usuario/authentication/login";
    }


    @GetMapping("/qrCodeAuth/{code}")
    public String qrCode(@PathVariable final String code, final Model model) throws UnsupportedEncodingException {
        Usuario usuario = usuarioService.findBySecret(code);
        if (usuario == null) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("usuario.not.exists"));
            return "usuario/authentication/qrCodeAuth";
        }
        String otpUrl = OTP.getURL(usuario.getSecret(), 6, Type.TOTP, "Lablims", usuario.getUsername());
        byte[] image = new byte[0];
        try {
            image = QRCodeService.getQRCode(otpUrl, 250, 250);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("twoFaQrUrl", Base64.getEncoder().encodeToString(image));
        model.addAttribute("otpUrl", usuario.getSecret());
        return "usuario/authentication/qrCodeAuth";
    }

    @GetMapping("/alterarSenha/{code}")
    public String alterarSenha(@PathVariable final String code,  @ModelAttribute("oldPassword") String oldPassword,
                               @ModelAttribute("newPassword") String newPassword,
                               @ModelAttribute("confirmPassword") String confirmPassword, final Model model) {
        model.addAttribute("code", code);
        model.addAttribute("username", usuarioService.findBySecret(code).getUsername());
        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);
        model.addAttribute("confirmPassword", confirmPassword);
        return "usuario/authentication/alterarSenha";
    }

    @GetMapping("/changePass")
    public String changePass(final Model model) {
        return "usuario/authentication/changePass";
    }

    @PostMapping("/alterarSenha/{code}")
    public String alterarSenha(@PathVariable final String code,
                               @ModelAttribute("oldPassword") String oldPassword,
                               @ModelAttribute("newPassword") String newPassword,
                               @ModelAttribute("confirmPassword") String confirmPassword,
                               final Model model, final RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.findBySecret(code);
        if (usuario == null) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("usuario.not.exists"));
            return "usuario/authentication/alterarSenha";
        }
        if (usuarioService.validarUser(usuario.getUsername(), oldPassword)) {
            if (newPassword.equals(confirmPassword)) {
                CustomRevisionEntity.setMotivoText("Alteração de Senha");
                usuarioService.alterarSenha(usuario.getUsername(), newPassword);
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.password.not.match"));
                return "usuario/authentication/alterarSenha";
            }
        } else {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "usuario/authentication/alterarSenha";
        }
        return "redirect:/login";
    }

    @PostMapping("/changePass")
    public String changePass(@ModelAttribute("oldPassword") String oldPassword,
                             @ModelAttribute("newPassword") String newPassword,
                             @ModelAttribute("confirmPassword") String confirmPassword,
                             Principal principal,
                             final Model model, final RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.findByUsername(principal.getName());
        if (usuarioService.validarUser(usuario.getUsername(), oldPassword)) {
            if (newPassword.equals(confirmPassword)) {
                CustomRevisionEntity.setMotivoText("Alteração de Senha");
                usuarioService.alterarSenha(usuario.getUsername(), newPassword);
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.password.not.match"));
                return "redirect:/index";
            }
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "redirect:/index";
        }
        redirectAttributes.addFlashAttribute("sistema", sistemaRepository.findBySistemaNome("Lablims"));
        return "redirect:/index";
    }


}
