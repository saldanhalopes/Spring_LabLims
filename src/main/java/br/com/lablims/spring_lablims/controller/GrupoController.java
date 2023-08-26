package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.model.GrupoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.GrupoService;
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
@RequestMapping("/grupos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class GrupoController {

    private final GrupoService grupoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public GrupoController(final GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<GrupoDTO> grupos = grupoService.findAll(filter, pageable);
        model.addAttribute("grupos", grupos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(grupos));
        return "grupo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("grupo") final GrupoDTO grupoDTO) {
        return "grupo/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("grupo") @Valid final GrupoDTO grupoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "grupo/add";
        }
        grupoService.create(grupoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("grupo.create.success"));
        return "redirect:/grupos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("grupo", grupoService.get(id));
        return "grupo/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("grupo") @Valid final GrupoDTO grupoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "grupo/edit";
        }
        grupoService.update(id, grupoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("grupo.update.success"));
        return "redirect:/grupos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = grupoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            grupoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("grupo.delete.success"));
        }
        return "redirect:/grupos";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Grupo>> revisoes = genericRevisionRepository.listaRevisoes(Grupo.class);
        model.addAttribute("audits", revisoes);
        return "/grupo/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Grupo grupo = grupoService.findById(id);
        List<EntityRevision<Grupo>> revisoes = genericRevisionRepository.listaRevisoesById(grupo.getId(), Grupo.class);
        model.addAttribute("audits", revisoes);
        return "/grupo/audit";
    }

}
