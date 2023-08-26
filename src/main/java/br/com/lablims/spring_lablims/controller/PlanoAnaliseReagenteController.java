package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.domain.PlanoAnaliseReagente;
import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.model.PlanoAnaliseReagenteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ReagenteRepository;
import br.com.lablims.spring_lablims.service.PlanoAnaliseReagenteService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.beans.factory.annotation.Autowired;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.config.EntityRevision;

import java.util.List;


@Controller
@RequestMapping("/planoAnaliseReagentes")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class PlanoAnaliseReagenteController {

    private final PlanoAnaliseReagenteService planoAnaliseReagenteService;
    private final ReagenteRepository reagenteRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public PlanoAnaliseReagenteController(
            final PlanoAnaliseReagenteService planoAnaliseReagenteService,
            final ReagenteRepository reagenteRepository) {
        this.planoAnaliseReagenteService = planoAnaliseReagenteService;
        this.reagenteRepository = reagenteRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("reagenteValues", reagenteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Reagente::getId, Reagente::getCodigo)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<PlanoAnaliseReagenteDTO> planoAnaliseReagentes = planoAnaliseReagenteService.findAll(filter, pageable);
        model.addAttribute("planoAnaliseReagentes", planoAnaliseReagentes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(planoAnaliseReagentes));
        return "planoAnaliseReagente/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("planoAnaliseReagente") final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO) {
        return "planoAnaliseReagente/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("planoAnaliseReagente") @Valid final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "planoAnaliseReagente/add";
        }
        planoAnaliseReagenteService.create(planoAnaliseReagenteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnaliseReagente.create.success"));
        return "redirect:/planoAnaliseReagentes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("planoAnaliseReagente", planoAnaliseReagenteService.get(id));
        return "planoAnaliseReagente/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("planoAnaliseReagente") @Valid final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "planoAnaliseReagente/edit";
        }
        planoAnaliseReagenteService.update(id, planoAnaliseReagenteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnaliseReagente.update.success"));
        return "redirect:/planoAnaliseReagentes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        planoAnaliseReagenteService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("planoAnaliseReagente.delete.success"));
        return "redirect:/planoAnaliseReagentes";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<PlanoAnaliseReagente>> revisoes = genericRevisionRepository.listaRevisoes(PlanoAnaliseReagente.class);
        model.addAttribute("audits", revisoes);
        return "/planoAnaliseReagente/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        PlanoAnaliseReagente planoAnaliseReagente = planoAnaliseReagenteService.findById(id);
        List<EntityRevision<PlanoAnaliseReagente>> revisoes = genericRevisionRepository.listaRevisoesById(planoAnaliseReagente.getId(), PlanoAnaliseReagente.class);
        model.addAttribute("audits", revisoes);
        return "/planoAnaliseReagente/audit";
    }

}
