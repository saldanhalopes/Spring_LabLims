package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.ColunaConfig;
import br.com.lablims.spring_lablims.model.ColunaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaConfigRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.ColunaService;
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
@RequestMapping("/colunas")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class ColunaController {

    private final ColunaService colunaService;
    private final ColunaConfigRepository colunaConfigRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public ColunaController(final ColunaService colunaService,
            final ColunaConfigRepository colunaConfigRepository) {
        this.colunaService = colunaService;
        this.colunaConfigRepository = colunaConfigRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("tipoColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
        model.addAttribute("fabricanteColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
        model.addAttribute("marcaColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
        model.addAttribute("faseColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
        model.addAttribute("tamanhoColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
        model.addAttribute("diametroColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
        model.addAttribute("particulaColunaValues", colunaConfigRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaConfig::getId, ColunaConfig::getTipo)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<ColunaDTO> colunas = colunaService.findAll(filter, pageable);
        model.addAttribute("colunas", colunas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(colunas));
        return "coluna/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("coluna") final ColunaDTO colunaDTO) {
        return "coluna/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("coluna") @Valid final ColunaDTO colunaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "coluna/add";
        }
        colunaService.create(colunaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("coluna.create.success"));
        return "redirect:/colunas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("coluna", colunaService.get(id));
        return "coluna/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("coluna") @Valid final ColunaDTO colunaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "coluna/edit";
        }
        colunaService.update(id, colunaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("coluna.update.success"));
        return "redirect:/colunas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = colunaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            colunaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("coluna.delete.success"));
        }
        return "redirect:/colunas";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Coluna>> revisoes = genericRevisionRepository.listaRevisoes(Coluna.class);
        model.addAttribute("audits", revisoes);
        return "/coluna/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Coluna coluna = colunaService.findById(id);
        List<EntityRevision<Coluna>> revisoes = genericRevisionRepository.listaRevisoesById(coluna.getId(), Coluna.class);
        model.addAttribute("audits", revisoes);
        return "/coluna/audit";
    }

}
