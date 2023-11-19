package br.com.lablims.spring_lablims.controller.auth;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.UsuarioDTO;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.GrupoRepository;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final GrupoRepository grupoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("gruposValues", grupoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Grupo::getId, Grupo::getGrupo)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<UsuarioDTO> usuarios = usuarioService.findAll(filter, pageable);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(usuarios));
        return "usuario/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("usuario") final UsuarioDTO usuarioDTO) {
        return "pages/usuario/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("usuario") @Valid final UsuarioDTO usuarioDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (!bindingResult.hasFieldErrors("email") && usuarioService.emailExists(usuarioDTO.getEmail())) {
            bindingResult.rejectValue("email", "Exists.usuario.email");
        }
        if (!bindingResult.hasFieldErrors("username") && usuarioService.usernameExists(usuarioDTO.getUsername())) {
            bindingResult.rejectValue("username", "Exists.usuario.username");
        }
        if (bindingResult.hasErrors()) {
            return "usuario/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                usuarioService.create(usuarioDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("usuario.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "usuario/add";
            }
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("usuario", usuarioService.get(id));
        return "usuario/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("usuario") @Valid final UsuarioDTO usuarioDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes,
                       @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        final UsuarioDTO currentUsuarioDTO = usuarioService.get(id);
        if (!bindingResult.hasFieldErrors("email") &&
                !usuarioDTO.getEmail().equalsIgnoreCase(currentUsuarioDTO.getEmail()) &&
                usuarioService.emailExists(usuarioDTO.getEmail())) {
            bindingResult.rejectValue("email", "Exists.usuario.email");
        }
        if (!bindingResult.hasFieldErrors("username") &&
                !usuarioDTO.getUsername().equalsIgnoreCase(currentUsuarioDTO.getUsername()) &&
                usuarioService.usernameExists(usuarioDTO.getUsername())) {
            bindingResult.rejectValue("username", "Exists.usuario.username");
        }
        if (bindingResult.hasErrors()) {
            return "pages/usuario/edit";
        }if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            usuarioService.update(id, usuarioDTO);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("usuario.update.success"));
        } else {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "usuario/edit";
        }
        return "redirect:/usuarios";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = usuarioService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                usuarioService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("usuario.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/usuarios";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Usuario>> revisoes = genericRevisionRepository.listaRevisoes(Usuario.class);
        model.addAttribute("audits", revisoes);
        return "usuario/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Usuario usuario = usuarioService.findById(id);
        List<EntityRevision<Usuario>> revisoes = genericRevisionRepository.listaRevisoesById(usuario.getId(), Usuario.class);
        model.addAttribute("audits", revisoes);
        return "usuario/audit";
    }


}
