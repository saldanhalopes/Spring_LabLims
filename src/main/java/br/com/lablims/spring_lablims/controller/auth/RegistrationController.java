package br.com.lablims.spring_lablims.controller.auth;

import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.repos.GrupoRepository;
import br.com.lablims.spring_lablims.service.RegistrationService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.*;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Base64;


@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
@PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
public class RegistrationController {

    private final RegistrationService registrationService;

    private final UsuarioService usuarioService;

    private final GrupoRepository grupoRepository;

    private final EmailService emailService;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("grupoValues", grupoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Grupo::getId, Grupo::getRegra)));
    }

    @GetMapping
    public String register(@ModelAttribute final Usuario usuario) {
        return "usuario/registration/register";
    }

    @GetMapping("/qrCode/{user}")
    public String qrCode(@PathVariable final String user, final Model model)  {
        Usuario usuario = usuarioService.findByUsername(user);
        model.addAttribute("usuario", usuario);
        if (usuario == null) {
            return "redirect:/register/";
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
        return "usuario/registration/qrCode";
    }


    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping
    public String add(@ModelAttribute@Valid final Usuario usuario, final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) throws MessagingException {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("bindingResult.hasErrors"));
            return "usuario/registration/register";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Usuario");
                String newPass = OTP.randomBase32(8);
                usuario.setPassword(newPass);
                registrationService.register(usuario);
                enviarEmail(usuario.getEmail(), usuario.getUsername(), usuario.getSecret(), newPass);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("usuario.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "usuario/registration/register";
            }
        }
        return "redirect:/usuarios";
    }

    private void enviarEmail(String email, String username, String secret, String senha) throws MessagingException {
        emailService.sendHtmlMessage(email,"Usuario foi criado com sucesso.",
                HtmlRegisterContent.html(username, "http://localhost:8080/qrCodeAuth/" +  secret,senha));
    }


//  ALTERAR SENNHA
//        try {
//            int pwnedHashCount = registrationService.getPwnedHashCount(registrationDTO.getPassword());
//            if (pwnedHashCount > 0){
//                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.pass.invalid"));
//                return "usuario/registration/register";
//            }
//        } catch (HaveIBeenPwndException ex){
//            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.pass.invalid"));
//            return "usuario/registration/register";
//        }
//    }


}
