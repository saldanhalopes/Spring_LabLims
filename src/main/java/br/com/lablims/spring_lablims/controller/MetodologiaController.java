package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.domain.Metodologia;
import br.com.lablims.spring_lablims.model.MetodologiaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.MetodologiaRepository;
import br.com.lablims.spring_lablims.service.MetodologiaService;
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
@RequestMapping("/metodologias")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class MetodologiaController {

    private final MetodologiaService metodologiaService;
    private final CategoriaMetodologiaRepository categoriaMetodologiaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;


    public MetodologiaController(final MetodologiaService metodologiaService,
                                 final CategoriaMetodologiaRepository categoriaMetodologiaRepository) {
        this.metodologiaService = metodologiaService;
        this.categoriaMetodologiaRepository = categoriaMetodologiaRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("MetodologiaValues", categoriaMetodologiaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(CategoriaMetodologia::getId, CategoriaMetodologia::getCategoria)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<MetodologiaDTO> metodologias = metodologiaService.findAll(filter, pageable);
        model.addAttribute("metodologias", metodologias);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(metodologias));
        return "metodologia/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("metodologia") final MetodologiaDTO metodologiaDTO) {
        return "metodologia/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("metodologia") @Valid final MetodologiaDTO metodologiaDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "metodologia/add";
        }
        metodologiaService.create(metodologiaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("metodologia.create.success"));
        return "redirect:/metodologias";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("metodologia", metodologiaService.get(id));
        return "metodologia/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("metodologia") @Valid final MetodologiaDTO metodologiaDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "metodologia/edit";
        }
        metodologiaService.update(id, metodologiaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("metodologia.update.success"));
        return "redirect:/metodologias";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes) {
        final String referencedWarning = metodologiaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            metodologiaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("metodologia.delete.success"));
        }
        return "redirect:/metodologias";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Metodologia>> revisoes = genericRevisionRepository.listaRevisoes(Metodologia.class);
        model.addAttribute("audits", revisoes);
        return "/metodologia/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Metodologia metodologia = metodologiaService.findById(id);
        List<EntityRevision<Metodologia>> revisoes = genericRevisionRepository.listaRevisoesById(metodologia.getId(), Metodologia.class);
        model.addAttribute("audits", revisoes);
        return "/metodologia/audit";
    }

}
