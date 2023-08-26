package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.PlanoAnaliseDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseRepository;
import br.com.lablims.spring_lablims.repos.AnaliseTipoRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVesaoRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.service.PlanoAnaliseService;
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
@RequestMapping("/planoAnalises")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class PlanoAnaliseController {

    private final PlanoAnaliseService planoAnaliseService;
    private final MetodologiaVesaoRepository metodologiaVesaoRepository;
    private final AnaliseRepository analiseRepository;
    private final AnaliseTipoRepository analiseTipoRepository;
    private final SetorRepository setorRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;
    
    public PlanoAnaliseController(final PlanoAnaliseService planoAnaliseService,
            final MetodologiaVesaoRepository metodologiaVesaoRepository,
            final AnaliseRepository analiseRepository,
            final AnaliseTipoRepository analiseTipoRepository,
            final SetorRepository setorRepository) {
        this.planoAnaliseService = planoAnaliseService;
        this.metodologiaVesaoRepository = metodologiaVesaoRepository;
        this.analiseRepository = analiseRepository;
        this.analiseTipoRepository = analiseTipoRepository;
        this.setorRepository = setorRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("metodologiaVersaoValues", metodologiaVesaoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MetodologiaVesao::getId, MetodologiaVesao::getObs)));
        model.addAttribute("analiseValues", analiseRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Analise::getId, Analise::getAnalise)));
        model.addAttribute("analiseTipoValues", analiseTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AnaliseTipo::getId, AnaliseTipo::getAnaliseTipo)));
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<PlanoAnaliseDTO> planoAnalises = planoAnaliseService.findAll(filter, pageable);
        model.addAttribute("planoAnalises", planoAnalises);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(planoAnalises));
        return "planoAnalise/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("planoAnalise") final PlanoAnaliseDTO planoAnaliseDTO) {
        return "planoAnalise/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("planoAnalise") @Valid final PlanoAnaliseDTO planoAnaliseDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "planoAnalise/add";
        }
        planoAnaliseService.create(planoAnaliseDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnalise.create.success"));
        return "redirect:/planoAnalises";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("planoAnalise", planoAnaliseService.get(id));
        return "planoAnalise/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("planoAnalise") @Valid final PlanoAnaliseDTO planoAnaliseDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "planoAnalise/edit";
        }
        planoAnaliseService.update(id, planoAnaliseDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnalise.update.success"));
        return "redirect:/planoAnalises";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = planoAnaliseService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            planoAnaliseService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("planoAnalise.delete.success"));
        }
        return "redirect:/planoAnalises";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<PlanoAnalise>> revisoes = genericRevisionRepository.listaRevisoes(PlanoAnalise.class);
        model.addAttribute("audits", revisoes);
        return "/planoAnalise/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        PlanoAnalise planoAnalise = planoAnaliseService.findById(id);
        List<EntityRevision<PlanoAnalise>> revisoes = genericRevisionRepository.listaRevisoesById(planoAnalise.getId(), PlanoAnalise.class);
        model.addAttribute("audits", revisoes);
        return "/planoAnalise/audit";
    }

}
