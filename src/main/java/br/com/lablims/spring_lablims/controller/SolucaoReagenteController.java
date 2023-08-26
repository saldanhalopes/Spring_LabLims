package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.domain.SolucaoReagente;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoReagenteDTO;
import br.com.lablims.spring_lablims.repos.ReagenteRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.SolucaoReagenteService;
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
@RequestMapping("/solucaoReagentes")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class SolucaoReagenteController {

    private final SolucaoReagenteService solucaoReagenteService;
    private final SolucaoRegistroRepository solucaoRegistroRepository;
    private final ReagenteRepository reagenteRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public SolucaoReagenteController(final SolucaoReagenteService solucaoReagenteService,
            final SolucaoRegistroRepository solucaoRegistroRepository,
            final ReagenteRepository reagenteRepository,
            final UnidadeMedidaRepository unidadeMedidaRepository) {
        this.solucaoReagenteService = solucaoReagenteService;
        this.solucaoRegistroRepository = solucaoRegistroRepository;
        this.reagenteRepository = reagenteRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("solucaoRegistroValues", solucaoRegistroRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(SolucaoRegistro::getId, SolucaoRegistro::getDescricao)));
        model.addAttribute("reagenteValues", reagenteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Reagente::getId, Reagente::getCodigo)));
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<SolucaoReagenteDTO> solucaoReagentes = solucaoReagenteService.findAll(filter, pageable);
        model.addAttribute("solucaoReagentes", solucaoReagentes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(solucaoReagentes));
        return "solucaoReagente/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("solucaoReagente") final SolucaoReagenteDTO solucaoReagenteDTO) {
        return "solucaoReagente/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("solucaoReagente") @Valid final SolucaoReagenteDTO solucaoReagenteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "solucaoReagente/add";
        }
        solucaoReagenteService.create(solucaoReagenteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoReagente.create.success"));
        return "redirect:/solucaoReagentes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("solucaoReagente", solucaoReagenteService.get(id));
        return "solucaoReagente/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("solucaoReagente") @Valid final SolucaoReagenteDTO solucaoReagenteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "solucaoReagente/edit";
        }
        solucaoReagenteService.update(id, solucaoReagenteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoReagente.update.success"));
        return "redirect:/solucaoReagentes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        solucaoReagenteService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("solucaoReagente.delete.success"));
        return "redirect:/solucaoReagentes";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<SolucaoReagente>> revisoes = genericRevisionRepository.listaRevisoes(SolucaoReagente.class);
        model.addAttribute("audits", revisoes);
        return "/solucaoReagente/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        SolucaoReagente solucaoReagente = solucaoReagenteService.findById(id);
        List<EntityRevision<SolucaoReagente>> revisoes = genericRevisionRepository.listaRevisoesById(solucaoReagente.getId(), SolucaoReagente.class);
        model.addAttribute("audits", revisoes);
        return "/solucaoReagente/audit";
    }

}
