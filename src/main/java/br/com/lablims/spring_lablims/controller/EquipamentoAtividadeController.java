package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.EquipamentoAtividade;
import br.com.lablims.spring_lablims.model.EquipamentoAtividadeDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.EquipamentoAtividadeService;
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
@RequestMapping("/equipamentoAtividades")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class EquipamentoAtividadeController {

    private final EquipamentoAtividadeService equipamentoAtividadeService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public EquipamentoAtividadeController(
            final EquipamentoAtividadeService equipamentoAtividadeService) {
        this.equipamentoAtividadeService = equipamentoAtividadeService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<EquipamentoAtividadeDTO> equipamentoAtividades = equipamentoAtividadeService.findAll(filter, pageable);
        model.addAttribute("equipamentoAtividades", equipamentoAtividades);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentoAtividades));
        return "equipamentoAtividade/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("equipamentoAtividade") final EquipamentoAtividadeDTO equipamentoAtividadeDTO) {
        return "equipamentoAtividade/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("equipamentoAtividade") @Valid final EquipamentoAtividadeDTO equipamentoAtividadeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamentoAtividade/add";
        }
        equipamentoAtividadeService.create(equipamentoAtividadeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoAtividade.create.success"));
        return "redirect:/equipamentoAtividades";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamentoAtividade", equipamentoAtividadeService.get(id));
        return "equipamentoAtividade/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("equipamentoAtividade") @Valid final EquipamentoAtividadeDTO equipamentoAtividadeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamentoAtividade/edit";
        }
        equipamentoAtividadeService.update(id, equipamentoAtividadeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoAtividade.update.success"));
        return "redirect:/equipamentoAtividades";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = equipamentoAtividadeService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            equipamentoAtividadeService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamentoAtividade.delete.success"));
        }
        return "redirect:/equipamentoAtividades";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<EquipamentoAtividade>> revisoes = genericRevisionRepository.listaRevisoes(EquipamentoAtividade.class);
        model.addAttribute("audits", revisoes);
        return "/equipamentoAtividade/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        EquipamentoAtividade equipamentoAtividade = equipamentoAtividadeService.findById(id);
        List<EntityRevision<EquipamentoAtividade>> revisoes = genericRevisionRepository.listaRevisoesById(equipamentoAtividade.getId(), EquipamentoAtividade.class);
        model.addAttribute("audits", revisoes);
        return "/equipamentoAtividade/audit";
    }

}
