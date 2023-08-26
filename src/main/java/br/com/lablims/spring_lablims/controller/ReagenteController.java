package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.ReagenteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ArquivosRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.ReagenteService;
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
@RequestMapping("/reagentes")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class ReagenteController {

    private final ReagenteService reagenteService;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public ReagenteController(final ReagenteService reagenteService,
            final UnidadeMedidaRepository unidadeMedidaRepository,
            final ArquivosRepository arquivosRepository) {
        this.reagenteService = reagenteService;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.arquivosRepository = arquivosRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
        model.addAttribute("fdsValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<ReagenteDTO> reagentes = reagenteService.findAll(filter, pageable);
        model.addAttribute("reagentes", reagentes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(reagentes));
        return "reagente/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("reagente") final ReagenteDTO reagenteDTO) {
        return "reagente/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("reagente") @Valid final ReagenteDTO reagenteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "reagente/add";
        }
        reagenteService.create(reagenteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("reagente.create.success"));
        return "redirect:/reagentes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("reagente", reagenteService.get(id));
        return "reagente/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("reagente") @Valid final ReagenteDTO reagenteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "reagente/edit";
        }
        reagenteService.update(id, reagenteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("reagente.update.success"));
        return "redirect:/reagentes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = reagenteService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            reagenteService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("reagente.delete.success"));
        }
        return "redirect:/reagentes";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Reagente>> revisoes = genericRevisionRepository.listaRevisoes(Reagente.class);
        model.addAttribute("audits", revisoes);
        return "/reagente/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Reagente reagent = reagenteService.findById(id);
        List<EntityRevision<Reagente>> revisoes = genericRevisionRepository.listaRevisoesById(reagent.getId(), Reagente.class);
        model.addAttribute("audits", revisoes);
        return "/reagente/audit";
    }

}
