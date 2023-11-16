package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.CelulaTipo;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.model.CelulaTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.CelulaTipoService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/celulaTipos")
public class CelulaTipoController {

    private final CelulaTipoService celulaTipoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<CelulaTipoDTO> celulaTipos = celulaTipoService.findAll(filter, pageable);
        model.addAttribute("celulaTipos", celulaTipos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(celulaTipos));
        return "parameters/celulaTipo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("celulaTipo") final CelulaTipoDTO celulaTipoDTO) {
        return "parameters/celulaTipo/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("celulaTipo") @Valid final CelulaTipoDTO celulaTipoDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/celulaTipo/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                celulaTipoService.create(celulaTipoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("celulaTipo.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/celulaTipo/add";
            }
        }
        return "redirect:/celulaTipos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("celulaTipo", celulaTipoService.get(id));
        return "parameters/celulaTipo/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("celulaTipo") @Valid final CelulaTipoDTO celulaTipoDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/celulaTipo/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                celulaTipoService.update(id, celulaTipoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("celulaTipo.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/celulaTipo/edit";
            }
        }
        return "redirect:/celulaTipos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = celulaTipoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                celulaTipoService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("celulaTipo.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/celulaTipos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<CelulaTipo>> revisoes = genericRevisionRepository.listaRevisoes(CelulaTipo.class);
        model.addAttribute("audits", revisoes);
        return "parameters/celulaTipo/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        CelulaTipo celulaTipo = celulaTipoService.findById(id);
        List<EntityRevision<CelulaTipo>> revisoes = genericRevisionRepository.listaRevisoesById(celulaTipo.getId(), CelulaTipo.class);
        model.addAttribute("audits", revisoes);
        return "parameters/celulaTipo/audit";
    }

}
