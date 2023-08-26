package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.LoteStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.LoteStatusService;
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
@RequestMapping("/loteStatuss")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class LoteStatusController {

    private final LoteStatusService loteStatusService;
    private final LoteRepository loteRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final AnaliseStatusRepository analiseStatusRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public LoteStatusController(final LoteStatusService loteStatusService,
            final LoteRepository loteRepository,
            final PlanoAnaliseRepository planoAnaliseRepository,
            final AnaliseStatusRepository analiseStatusRepository,
            final UsuarioRepository usuarioRepository) {
        this.loteStatusService = loteStatusService;
        this.loteRepository = loteRepository;
        this.planoAnaliseRepository = planoAnaliseRepository;
        this.analiseStatusRepository = analiseStatusRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("loteValues", loteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Lote::getId, Lote::getLote)));
        model.addAttribute("planoAnaliseValues", planoAnaliseRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(PlanoAnalise::getId, PlanoAnalise::getDescricao)));
        model.addAttribute("analiseStatusValues", analiseStatusRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AnaliseStatus::getId, AnaliseStatus::getAnaliseStatus)));
        model.addAttribute("conferente1Values", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("conferente2Values", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<LoteStatusDTO> loteStatuss = loteStatusService.findAll(filter, pageable);
        model.addAttribute("loteStatuss", loteStatuss);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(loteStatuss));
        return "loteStatus/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("loteStatus") final LoteStatusDTO loteStatusDTO) {
        return "loteStatus/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("loteStatus") @Valid final LoteStatusDTO loteStatusDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "loteStatus/add";
        }
        loteStatusService.create(loteStatusDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("loteStatus.create.success"));
        return "redirect:/loteStatuss";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("loteStatus", loteStatusService.get(id));
        return "loteStatus/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("loteStatus") @Valid final LoteStatusDTO loteStatusDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "loteStatus/edit";
        }
        loteStatusService.update(id, loteStatusDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("loteStatus.update.success"));
        return "redirect:/loteStatuss";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        loteStatusService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("loteStatus.delete.success"));
        return "redirect:/loteStatuss";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<LoteStatus>> revisoes = genericRevisionRepository.listaRevisoes(LoteStatus.class);
        model.addAttribute("audits", revisoes);
        return "/loteStatus/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        LoteStatus xxx = loteStatusService.findById(id);
        List<EntityRevision<LoteStatus>> revisoes = genericRevisionRepository.listaRevisoesById(xxx.getId(), LoteStatus.class);
        model.addAttribute("audits", revisoes);
        return "/loteStatus/audit";
    }

}
