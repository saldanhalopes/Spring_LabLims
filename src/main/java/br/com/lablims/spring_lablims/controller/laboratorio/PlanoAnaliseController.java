package br.com.lablims.spring_lablims.controller.laboratorio;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.PlanoAnaliseDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.PlanoAnaliseService;
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
@RequestMapping("/planoAnalises")
public class PlanoAnaliseController {

    private final PlanoAnaliseService planoAnaliseService;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final AnaliseRepository analiseRepository;
    private final AnaliseTipoRepository analiseTipoRepository;
    private final SetorRepository setorRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public PlanoAnaliseController(final PlanoAnaliseService planoAnaliseService,
            final MetodologiaVersaoRepository metodologiaVersaoRepository,
            final AnaliseRepository analiseRepository,
            final AnaliseTipoRepository analiseTipoRepository,
            final SetorRepository setorRepository) {
        this.planoAnaliseService = planoAnaliseService;
        this.metodologiaVersaoRepository = metodologiaVersaoRepository;
        this.analiseRepository = analiseRepository;
        this.analiseTipoRepository = analiseTipoRepository;
        this.setorRepository = setorRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("metodologiaVersaoValues", metodologiaVersaoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MetodologiaVersao::getId, MetodologiaVersao::getObs)));
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


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<PlanoAnaliseDTO> planoAnalises = planoAnaliseService.findAll(filter, pageable);
        model.addAttribute("planoAnalises", planoAnalises);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(planoAnalises));
        return "pages/planoAnalise/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("planoAnalise") final PlanoAnaliseDTO planoAnaliseDTO) {
        return "pages/planoAnalise/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("planoAnalise") @Valid final PlanoAnaliseDTO planoAnaliseDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/planoAnalise/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                planoAnaliseService.create(planoAnaliseDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnalise.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/planoAnalise/add";
            }
        }
        return "redirect:/planoAnalises";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("planoAnalise", planoAnaliseService.get(id));
        return "pages/planoAnalise/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("planoAnalise") @Valid final PlanoAnaliseDTO planoAnaliseDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/planoAnalise/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                planoAnaliseService.update(id, planoAnaliseDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnalise.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/planoAnalise/edit";
            }
        }
        return "redirect:/planoAnalises";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = planoAnaliseService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                planoAnaliseService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("planoAnalise.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/planoAnalises";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<PlanoAnalise>> revisoes = genericRevisionRepository.listaRevisoes(PlanoAnalise.class);
        model.addAttribute("audits", revisoes);
        return "pages/planoAnalise/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        PlanoAnalise planoAnalise = planoAnaliseService.findById(id);
        List<EntityRevision<PlanoAnalise>> revisoes = genericRevisionRepository.listaRevisoesById(planoAnalise.getId(), PlanoAnalise.class);
        model.addAttribute("audits", revisoes);
        return "pages/planoAnalise/audit";
    }


}
