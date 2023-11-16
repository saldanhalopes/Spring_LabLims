package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.Grandeza;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.UnidadeMedidaDTO;
import br.com.lablims.spring_lablims.repos.GrandezaRepository;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.UnidadeMedidaService;
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
@RequestMapping("/unidadeMedidas")
public class UnidadeMedidaController {

    private final UnidadeMedidaService unidadeMedidaService;
    private final GrandezaRepository grandezaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("grandezaValues", grandezaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Grandeza::getId, Grandeza::getGrandeza)));
    }


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<UnidadeMedidaDTO> unidadeMedidas = unidadeMedidaService.findAll(filter, pageable);
        model.addAttribute("unidadeMedidas", unidadeMedidas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(unidadeMedidas));
        return "parameters/unidadeMedida/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("unidadeMedida") final UnidadeMedidaDTO unidadeMedidaDTO) {
        return "parameters/unidadeMedida/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("unidadeMedida") @Valid final UnidadeMedidaDTO unidadeMedidaDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/unidadeMedida/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                unidadeMedidaService.create(unidadeMedidaDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("unidadeMedida.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/unidadeMedida/add";
            }
        }
        return "redirect:/unidadeMedidas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("unidadeMedida", unidadeMedidaService.get(id));
        return "parameters/unidadeMedida/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("unidadeMedida") @Valid final UnidadeMedidaDTO unidadeMedidaDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/unidadeMedida/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                unidadeMedidaService.update(id, unidadeMedidaDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("unidadeMedida.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/unidadeMedida/edit";
            }
        }
        return "redirect:/unidadeMedidas";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = unidadeMedidaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                unidadeMedidaService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("unidadeMedida.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/unidadeMedidas";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<UnidadeMedida>> revisoes = genericRevisionRepository.listaRevisoes(UnidadeMedida.class);
        model.addAttribute("audits", revisoes);
        return "parameters/unidadeMedida/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        UnidadeMedida unidadeMedida = unidadeMedidaService.findById(id);
        List<EntityRevision<UnidadeMedida>> revisoes = genericRevisionRepository.listaRevisoesById(unidadeMedida.getId(), UnidadeMedida.class);
        model.addAttribute("audits", revisoes);
        return "parameters/unidadeMedida/audit";
    }


}
