package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.EquipamentoTipo;
import br.com.lablims.spring_lablims.model.EquipamentoTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.EquipamentoTipoService;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/equipamentoTipos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class EquipamentoTipoController {

    private final EquipamentoTipoService equipamentoTipoService;
    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public EquipamentoTipoController(final EquipamentoTipoService equipamentoTipoService) {
        this.equipamentoTipoService = equipamentoTipoService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<EquipamentoTipoDTO> equipamentoTipos = equipamentoTipoService.findAll(filter, pageable);
        model.addAttribute("equipamentoTipos", equipamentoTipos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentoTipos));
        return "equipamentoTipo/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("equipamentoTipo") final EquipamentoTipoDTO equipamentoTipoDTO) {
        return "equipamentoTipo/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("equipamentoTipo") @Valid final EquipamentoTipoDTO equipamentoTipoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamentoTipo/add";
        }
        equipamentoTipoService.create(equipamentoTipoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoTipo.create.success"));
        return "redirect:/equipamentoTipos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamentoTipo", equipamentoTipoService.get(id));
        return "equipamentoTipo/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("equipamentoTipo") @Valid final EquipamentoTipoDTO equipamentoTipoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamentoTipo/edit";
        }
        equipamentoTipoService.update(id, equipamentoTipoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoTipo.update.success"));
        return "redirect:/equipamentoTipos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = equipamentoTipoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            equipamentoTipoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamentoTipo.delete.success"));
        }
        return "redirect:/equipamentoTipos";
    }
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<EquipamentoTipo>> revisoes = genericRevisionRepository.listaRevisoes(EquipamentoTipo.class);
        model.addAttribute("audits", revisoes);
        return "/equipamentoTipo/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        EquipamentoTipo equipamentoTipo = equipamentoTipoService.findById(id);
        List<EntityRevision<EquipamentoTipo>> revisoes = genericRevisionRepository.listaRevisoesById(equipamentoTipo.getId(), EquipamentoTipo.class);
        model.addAttribute("audits", revisoes);
        return "/equipamentoTipo/audit";
    }
}
