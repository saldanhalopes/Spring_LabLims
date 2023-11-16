package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.ReagenteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ArquivosRepository;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.ReagenteService;
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
@RequestMapping("/reagentes")
public class ReagenteController {

    private final ReagenteService reagenteService;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
        model.addAttribute("fdsValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<ReagenteDTO> reagentes = reagenteService.findAll(filter, pageable);
        model.addAttribute("reagentes", reagentes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(reagentes));
        return "parameters/reagente/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("reagente") final ReagenteDTO reagenteDTO) {
        return "parameters/reagente/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("reagente") @Valid final ReagenteDTO reagenteDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/reagente/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                reagenteService.create(reagenteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("reagente.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/reagente/add";
            }
        }
        return "redirect:/reagentes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("reagente", reagenteService.get(id));
        return "parameters/reagente/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("reagente") @Valid final ReagenteDTO reagenteDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/reagente/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                reagenteService.update(id, reagenteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("reagente.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/reagente/edit";
            }
        }
        return "redirect:/reagentes";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = reagenteService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                reagenteService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("reagente.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/reagentes";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Reagente>> revisoes = genericRevisionRepository.listaRevisoes(Reagente.class);
        model.addAttribute("audits", revisoes);
        return "parameters/reagente/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Reagente reagente = reagenteService.findById(id);
        List<EntityRevision<Reagente>> revisoes = genericRevisionRepository.listaRevisoesById(reagente.getId(), Reagente.class);
        model.addAttribute("audits", revisoes);
        return "parameters/reagente/audit";
    }

}
