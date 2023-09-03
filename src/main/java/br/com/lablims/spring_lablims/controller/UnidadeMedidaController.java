package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.EscalaMedida;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.UnidadeMedidaDTO;
import br.com.lablims.spring_lablims.repos.EscalaMedidaRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.UnidadeMedidaService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;


@Controller
@RequestMapping("/unidadeMedidas")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class UnidadeMedidaController {

    private final UnidadeMedidaService unidadeMedidaService;
    private final EscalaMedidaRepository escalaMedidaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public UnidadeMedidaController(final UnidadeMedidaService unidadeMedidaService,
            final EscalaMedidaRepository escalaMedidaRepository) {
        this.unidadeMedidaService = unidadeMedidaService;
        this.escalaMedidaRepository = escalaMedidaRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("escalaMedidaValues", escalaMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EscalaMedida::getId, EscalaMedida::getEscala)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<UnidadeMedidaDTO> unidadeMedidas = unidadeMedidaService.findAll(filter, pageable);
        model.addAttribute("unidadeMedidas", unidadeMedidas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(unidadeMedidas));
        return "unidadeMedida/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("unidadeMedida") final UnidadeMedidaDTO unidadeMedidaDTO) {
        return "unidadeMedida/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("unidadeMedida") @Valid final UnidadeMedidaDTO unidadeMedidaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "unidadeMedida/add";
        }
        unidadeMedidaService.create(unidadeMedidaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("unidadeMedida.create.success"));
        return "redirect:/unidadeMedidas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("unidadeMedida", unidadeMedidaService.get(id));
        return "unidadeMedida/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("unidadeMedida") @Valid final UnidadeMedidaDTO unidadeMedidaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "unidadeMedida/edit";
        }
        unidadeMedidaService.update(id, unidadeMedidaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("unidadeMedida.update.success"));
        return "redirect:/unidadeMedidas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = unidadeMedidaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            unidadeMedidaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("unidadeMedida.delete.success"));
        }
        return "redirect:/unidadeMedidas";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<UnidadeMedida>> revisoes = genericRevisionRepository.listaRevisoes(UnidadeMedida.class);
        model.addAttribute("audits", revisoes);
        return "/unidadeMedida/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        UnidadeMedida unidadeMedida = unidadeMedidaService.findById(id);
        List<EntityRevision<UnidadeMedida>> revisoes = genericRevisionRepository.listaRevisoesById(unidadeMedida.getId(), UnidadeMedida.class);
        model.addAttribute("audits", revisoes);
        return "/unidadeMedida/audit";
    }

}
