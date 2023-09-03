package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Campanha;
import br.com.lablims.spring_lablims.domain.Celula;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.model.CampanhaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CelulaRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.LoteRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.service.CampanhaService;
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
@RequestMapping("/campanhas")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class CampanhaController {

    private final CampanhaService campanhaService;
    private final SetorRepository setorRepository;
    private final CelulaRepository celulaRepository;
    private final LoteRepository loteRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public CampanhaController(final CampanhaService campanhaService,
            final SetorRepository setorRepository, final CelulaRepository celulaRepository,
            final LoteRepository loteRepository) {
        this.campanhaService = campanhaService;
        this.setorRepository = setorRepository;
        this.celulaRepository = celulaRepository;
        this.loteRepository = loteRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
        model.addAttribute("celulaValues", celulaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Celula::getId, Celula::getCelula)));
        model.addAttribute("lotesValues", loteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Lote::getId, Lote::getLote)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<CampanhaDTO> campanhas = campanhaService.findAll(filter, pageable);
        model.addAttribute("campanhas", campanhas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(campanhas));
        return "campanha/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("campanha") final CampanhaDTO campanhaDTO) {
        return "campanha/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("campanha") @Valid final CampanhaDTO campanhaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "campanha/add";
        }
        campanhaService.create(campanhaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("campanha.create.success"));
        return "redirect:/campanhas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("campanha", campanhaService.get(id));
        return "campanha/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("campanha") @Valid final CampanhaDTO campanhaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "campanha/edit";
        }
        campanhaService.update(id, campanhaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("campanha.update.success"));
        return "redirect:/campanhas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = campanhaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            campanhaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("campanha.delete.success"));
        }
        return "redirect:/campanhas";
    }


    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Campanha>> revisoes = genericRevisionRepository.listaRevisoes(Campanha.class);
        model.addAttribute("audits", revisoes);
        return "/campanha/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Campanha campanha = campanhaService.findById(id);
        List<EntityRevision<Campanha>> revisoes = genericRevisionRepository.listaRevisoesById(campanha.getId(), Campanha.class);
        model.addAttribute("audits", revisoes);
        return "/campanha/audit";
    }

}
