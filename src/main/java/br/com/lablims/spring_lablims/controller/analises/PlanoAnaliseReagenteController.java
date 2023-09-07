package br.com.lablims.spring_lablims.controller.analises;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.PlanoAnaliseReagente;
import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.model.PlanoAnaliseReagenteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.ReagenteRepository;
import br.com.lablims.spring_lablims.service.PlanoAnaliseReagenteService;
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
@RequestMapping("/planoAnaliseReagentes")
public class PlanoAnaliseReagenteController {

    private final PlanoAnaliseReagenteService planoAnaliseReagenteService;
    private final ReagenteRepository reagenteRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public PlanoAnaliseReagenteController(
            final PlanoAnaliseReagenteService planoAnaliseReagenteService,
            final ReagenteRepository reagenteRepository) {
        this.planoAnaliseReagenteService = planoAnaliseReagenteService;
        this.reagenteRepository = reagenteRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("reagenteValues", reagenteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Reagente::getId, Reagente::getCodigo)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<PlanoAnaliseReagenteDTO> planoAnaliseReagentes = planoAnaliseReagenteService.findAll(filter, pageable);
        model.addAttribute("planoAnaliseReagentes", planoAnaliseReagentes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(planoAnaliseReagentes));
        return "pages/planoAnaliseReagente/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("planoAnaliseReagente") final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO) {
        return "pages/planoAnaliseReagente/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("planoAnaliseReagente") @Valid final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO, final Model model,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/planoAnaliseReagente/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                planoAnaliseReagenteService.create(planoAnaliseReagenteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnaliseReagente.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "pages/planoAnaliseReagente/add";
            }
        }
        return "redirect:/planoAnaliseReagentes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("planoAnaliseReagente", planoAnaliseReagenteService.get(id));
        return "pages/planoAnaliseReagente/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("planoAnaliseReagente") @Valid final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/planoAnaliseReagente/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                planoAnaliseReagenteService.update(id, planoAnaliseReagenteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnaliseReagente.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "pages/planoAnaliseReagente/edit";
            }
        }
        return "redirect:/planoAnaliseReagentes";
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
            planoAnaliseReagenteService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("planoAnaliseReagente.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
        }
        return "redirect:/planoAnaliseReagentes";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<PlanoAnaliseReagente>> revisoes = genericRevisionRepository.listaRevisoes(PlanoAnaliseReagente.class);
        model.addAttribute("audits", revisoes);
        return "pages/planoAnaliseReagente/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        PlanoAnaliseReagente planoAnaliseReagente = planoAnaliseReagenteService.findById(id);
        List<EntityRevision<PlanoAnaliseReagente>> revisoes = genericRevisionRepository.listaRevisoesById(planoAnaliseReagente.getId(), PlanoAnaliseReagente.class);
        model.addAttribute("audits", revisoes);
        return "pages/planoAnaliseReagente/audit";
    }


}
