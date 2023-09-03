package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.model.ArquivosDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.ArquivosService;
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
@RequestMapping("/arquivoss")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class ArquivosController {

    private final ArquivosService arquivosService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public ArquivosController(final ArquivosService arquivosService) {
        this.arquivosService = arquivosService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<ArquivosDTO> arquivoss = arquivosService.findAll(filter, pageable);
        model.addAttribute("arquivoss", arquivoss);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(arquivoss));
        return "arquivos/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("arquivos") final ArquivosDTO arquivosDTO) {
        return "arquivos/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("arquivos") @Valid final ArquivosDTO arquivosDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "arquivos/add";
        }
        arquivosService.create(arquivosDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("arquivos.create.success"));
        return "redirect:/arquivoss";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("arquivos", arquivosService.get(id));
        return "arquivos/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("arquivos") @Valid final ArquivosDTO arquivosDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "arquivos/edit";
        }
        arquivosService.update(id, arquivosDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("arquivos.update.success"));
        return "redirect:/arquivoss";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = arquivosService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            arquivosService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("arquivos.delete.success"));
        }
        return "redirect:/arquivoss";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Arquivos>> revisoes = genericRevisionRepository.listaRevisoes(Arquivos.class);
        model.addAttribute("audits", revisoes);
        return "/arquivos/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Arquivos arquivos = arquivosService.findById(id);
        List<EntityRevision<Arquivos>> revisoes = genericRevisionRepository.listaRevisoesById(arquivos.getId(), Arquivos.class);
        model.addAttribute("audits", revisoes);
        return "/arquivos/audit";
    }

}
