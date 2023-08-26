package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.SolucaoTipo;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoTipoDTO;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.SolucaoTipoService;
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
@RequestMapping("/solucaoTipos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class SolucaoTipoController {

    private final SolucaoTipoService solucaoTipoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;


    public SolucaoTipoController(final SolucaoTipoService solucaoTipoService) {
        this.solucaoTipoService = solucaoTipoService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<SolucaoTipoDTO> solucaoTipos = solucaoTipoService.findAll(filter, pageable);
        model.addAttribute("solucaoTipos", solucaoTipos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(solucaoTipos));
        return "solucaoTipo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("solucaoTipo") final SolucaoTipoDTO solucaoTipoDTO) {
        return "solucaoTipo/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("solucaoTipo") @Valid final SolucaoTipoDTO solucaoTipoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "solucaoTipo/add";
        }
        solucaoTipoService.create(solucaoTipoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoTipo.create.success"));
        return "redirect:/solucaoTipos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("solucaoTipo", solucaoTipoService.get(id));
        return "solucaoTipo/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("solucaoTipo") @Valid final SolucaoTipoDTO solucaoTipoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "solucaoTipo/edit";
        }
        solucaoTipoService.update(id, solucaoTipoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoTipo.update.success"));
        return "redirect:/solucaoTipos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = solucaoTipoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            solucaoTipoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("solucaoTipo.delete.success"));
        }
        return "redirect:/solucaoTipos";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<SolucaoTipo>> revisoes = genericRevisionRepository.listaRevisoes(SolucaoTipo.class);
        model.addAttribute("audits", revisoes);
        return "/solucaoTipo/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        SolucaoTipo solucaoTipo = solucaoTipoService.findById(id);
        List<EntityRevision<SolucaoTipo>> revisoes = genericRevisionRepository.listaRevisoesById(solucaoTipo.getId(), SolucaoTipo.class);
        model.addAttribute("audits", revisoes);
        return "/solucaoTipo/audit";
    }

}
