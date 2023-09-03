package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.AmostraTipo;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.model.LoteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AmostraTipoRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.service.LoteService;
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
@RequestMapping("/lotes")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class LoteController {

    private final LoteService loteService;
    private final MaterialRepository materialRepository;
    private final AmostraTipoRepository amostraTipoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public LoteController(final LoteService loteService,
            final MaterialRepository materialRepository,
            final AmostraTipoRepository amostraTipoRepository) {
        this.loteService = loteService;
        this.materialRepository = materialRepository;
        this.amostraTipoRepository = amostraTipoRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("materialValues", materialRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Material::getId, Material::getFiscalizado)));
        model.addAttribute("amostraTipoValues", amostraTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AmostraTipo::getId, AmostraTipo::getTipo)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<LoteDTO> lotes = loteService.findAll(filter, pageable);
        model.addAttribute("lotes", lotes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(lotes));
        return "lote/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("lote") final LoteDTO loteDTO) {
        return "lote/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("lote") @Valid final LoteDTO loteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lote/add";
        }
        loteService.create(loteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lote.create.success"));
        return "redirect:/lotes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("lote", loteService.get(id));
        return "lote/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("lote") @Valid final LoteDTO loteDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lote/edit";
        }
        loteService.update(id, loteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lote.update.success"));
        return "redirect:/lotes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = loteService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            loteService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("lote.delete.success"));
        }
        return "redirect:/lotes";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Lote>> revisoes = genericRevisionRepository.listaRevisoes(Lote.class);
        model.addAttribute("audits", revisoes);
        return "/lote/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Lote lote = loteService.findById(id);
        List<EntityRevision<Lote>> revisoes = genericRevisionRepository.listaRevisoesById(lote.getId(), Lote.class);
        model.addAttribute("audits", revisoes);
        return "/lote/audit";
    }

}
