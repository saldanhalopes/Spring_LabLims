package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.AnaliseProdutividade;
import br.com.lablims.spring_lablims.domain.AnaliseStatus;
import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import br.com.lablims.spring_lablims.model.AnaliseStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseProdutividadeRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.AnaliseStatusService;
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
@RequestMapping("/analiseStatuss")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class AnaliseStatusController {

    private final AnaliseStatusService analiseStatusService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    private final AnaliseProdutividadeRepository analiseProdutividadeRepository;

    public AnaliseStatusController(final AnaliseStatusService analiseStatusService,
            final AnaliseProdutividadeRepository analiseProdutividadeRepository) {
        this.analiseStatusService = analiseStatusService;
        this.analiseProdutividadeRepository = analiseProdutividadeRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("analiseProdutividadeValues", analiseProdutividadeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AnaliseProdutividade::getId, AnaliseProdutividade::getAnaliseProdutividade)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<AnaliseStatusDTO> analiseStatuss = analiseStatusService.findAll(filter, pageable);
        model.addAttribute("analiseStatuss", analiseStatuss);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(analiseStatuss));
        return "analiseStatus/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("analiseStatus") final AnaliseStatusDTO analiseStatusDTO) {
        return "analiseStatus/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("analiseStatus") @Valid final AnaliseStatusDTO analiseStatusDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "analiseStatus/add";
        }
        analiseStatusService.create(analiseStatusDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("analiseStatus.create.success"));
        return "redirect:/analiseStatuss";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("analiseStatus", analiseStatusService.get(id));
        return "analiseStatus/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("analiseStatus") @Valid final AnaliseStatusDTO analiseStatusDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "analiseStatus/edit";
        }
        analiseStatusService.update(id, analiseStatusDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("analiseStatus.update.success"));
        return "redirect:/analiseStatuss";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = analiseStatusService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            analiseStatusService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("analiseStatus.delete.success"));
        }
        return "redirect:/analiseStatuss";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<AnaliseStatus>> revisoes = genericRevisionRepository.listaRevisoes(AnaliseStatus.class);
        model.addAttribute("audits", revisoes);
        return "/analiseStatus/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        AnaliseStatus analiseStatus = analiseStatusService.findById(id);
        List<EntityRevision<AnaliseStatus>> revisoes = genericRevisionRepository.listaRevisoesById(analiseStatus.getId(), AnaliseStatus.class);
        model.addAttribute("audits", revisoes);
        return "/analiseStatus/audit";
    }

}
