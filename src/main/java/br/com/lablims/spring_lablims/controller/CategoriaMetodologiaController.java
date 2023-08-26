package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.model.CategoriaMetodologiaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.CategoriaMetodologiaService;
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
@RequestMapping("/categoriaMetodologias")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class CategoriaMetodologiaController {

    private final CategoriaMetodologiaService categoriaMetodologiaService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public CategoriaMetodologiaController(
            final CategoriaMetodologiaService categoriaMetodologiaService) {
        this.categoriaMetodologiaService = categoriaMetodologiaService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<CategoriaMetodologiaDTO> categoriaMetodologias = categoriaMetodologiaService.findAll(filter, pageable);
        model.addAttribute("categoriaMetodologias", categoriaMetodologias);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(categoriaMetodologias));
        return "categoriaMetodologia/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("categoriaMetodologia") final CategoriaMetodologiaDTO categoriaMetodologiaDTO) {
        return "categoriaMetodologia/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("categoriaMetodologia") @Valid final CategoriaMetodologiaDTO categoriaMetodologiaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categoriaMetodologia/add";
        }
        categoriaMetodologiaService.create(categoriaMetodologiaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("categoriaMetodologia.create.success"));
        return "redirect:/categoriaMetodologias";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("categoriaMetodologia", categoriaMetodologiaService.get(id));
        return "categoriaMetodologia/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("categoriaMetodologia") @Valid final CategoriaMetodologiaDTO categoriaMetodologiaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categoriaMetodologia/edit";
        }
        categoriaMetodologiaService.update(id, categoriaMetodologiaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("categoriaMetodologia.update.success"));
        return "redirect:/categoriaMetodologias";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = categoriaMetodologiaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            categoriaMetodologiaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("categoriaMetodologia.delete.success"));
        }
        return "redirect:/categoriaMetodologias";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<CategoriaMetodologia>> revisoes = genericRevisionRepository.listaRevisoes(CategoriaMetodologia.class);
        model.addAttribute("audits", revisoes);
        return "/categoriaMetodologia/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        CategoriaMetodologia categoriaMetodologia = categoriaMetodologiaService.findById(id);
        List<EntityRevision<CategoriaMetodologia>> revisoes = genericRevisionRepository.listaRevisoesById(categoriaMetodologia.getId(), CategoriaMetodologia.class);
        model.addAttribute("audits", revisoes);
        return "/categoriaMetodologia/audit";
    }

}
