package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AmostraStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.AmostraStatusService;
import br.com.lablims.spring_lablims.service.UsuarioService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/loteStatuss")
public class LoteStatusController {

    private final AmostraStatusService amostraStatusService;
    private final LoteRepository loteRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final AnaliseStatusRepository analiseStatusRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public LoteStatusController(final AmostraStatusService amostraStatusService,
                                final LoteRepository loteRepository,
                                final PlanoAnaliseRepository planoAnaliseRepository,
                                final AnaliseStatusRepository analiseStatusRepository,
                                final UsuarioRepository usuarioRepository) {
        this.amostraStatusService = amostraStatusService;
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

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<AmostraStatusDTO> loteStatuss = amostraStatusService.findAll(filter, pageable);
        model.addAttribute("loteStatuss", loteStatuss);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(loteStatuss));
        return "parameters/loteStatus/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("loteStatus") final AmostraStatusDTO amostraStatusDTO) {
        return "parameters/loteStatus/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("loteStatus") @Valid final AmostraStatusDTO amostraStatusDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/loteStatus/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                amostraStatusService.create(amostraStatusDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("amostraStatus.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/loteStatus/add";
            }
        }
        return "redirect:/loteStatuss";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("loteStatus", amostraStatusService.get(id));
        return "parameters/loteStatus/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("loteStatus") @Valid final AmostraStatusDTO amostraStatusDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/loteStatus/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                amostraStatusService.update(id, amostraStatusDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("amostraStatus.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/loteStatus/edit";
            }
        }
        return "redirect:/loteStatuss";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            amostraStatusService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("amostraStatus.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/loteStatuss";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<AmostraStatus>> revisoes = genericRevisionRepository.listaRevisoes(AmostraStatus.class);
        model.addAttribute("audits", revisoes);
        return "parameters/loteStatus/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        AmostraStatus amostraStatus = amostraStatusService.findById(id);
        List<EntityRevision<AmostraStatus>> revisoes = genericRevisionRepository.listaRevisoesById(amostraStatus.getId(), AmostraStatus.class);
        model.addAttribute("audits", revisoes);
        return "parameters/loteStatus/audit";
    }

}
