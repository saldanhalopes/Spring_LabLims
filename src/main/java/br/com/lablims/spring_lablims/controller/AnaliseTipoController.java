package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import br.com.lablims.spring_lablims.model.AnaliseTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.AnaliseTipoService;
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
@RequestMapping("/analiseTipos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class AnaliseTipoController {

    private final AnaliseTipoService analiseTipoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public AnaliseTipoController(final AnaliseTipoService analiseTipoService) {
        this.analiseTipoService = analiseTipoService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<AnaliseTipoDTO> analiseTipos = analiseTipoService.findAll(filter, pageable);
        model.addAttribute("analiseTipos", analiseTipos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(analiseTipos));
        return "analiseTipo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("analiseTipo") final AnaliseTipoDTO analiseTipoDTO) {
        return "analiseTipo/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("analiseTipo") @Valid final AnaliseTipoDTO analiseTipoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "analiseTipo/add";
        }
        analiseTipoService.create(analiseTipoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("analiseTipo.create.success"));
        return "redirect:/analiseTipos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("analiseTipo", analiseTipoService.get(id));
        return "analiseTipo/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("analiseTipo") @Valid final AnaliseTipoDTO analiseTipoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "analiseTipo/edit";
        }
        analiseTipoService.update(id, analiseTipoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("analiseTipo.update.success"));
        return "redirect:/analiseTipos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = analiseTipoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            analiseTipoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("analiseTipo.delete.success"));
        }
        return "redirect:/analiseTipos";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<AnaliseTipo>> revisoes = genericRevisionRepository.listaRevisoes(AnaliseTipo.class);
        model.addAttribute("audits", revisoes);
        return "/analiseTipo/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        AnaliseTipo analiseTipo = analiseTipoService.findById(id);
        List<EntityRevision<AnaliseTipo>> revisoes = genericRevisionRepository.listaRevisoesById(analiseTipo.getId(), AnaliseTipo.class);
        model.addAttribute("audits", revisoes);
        return "/analiseTipo/audit";
    }

}
