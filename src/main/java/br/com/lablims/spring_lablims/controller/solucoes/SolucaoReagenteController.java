package br.com.lablims.spring_lablims.controller.solucoes;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoReagenteDTO;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.ReagenteRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.SolucaoReagenteService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/solucaoReagentes")
public class SolucaoReagenteController {

    private final SolucaoReagenteService solucaoReagenteService;
    private final SolucaoRegistroRepository solucaoRegistroRepository;
    private final ReagenteRepository reagenteRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

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


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<SolucaoReagenteDTO> solucaoReagentes = solucaoReagenteService.findAll(filter, pageable);
        model.addAttribute("solucaoReagentes", solucaoReagentes);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(solucaoReagentes));
        return "pages/solucaoReagente/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("solucaoReagente") final SolucaoReagenteDTO solucaoReagenteDTO) {
        return "pages/solucaoReagente/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("solucaoReagente") @Valid final SolucaoReagenteDTO solucaoReagenteDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/solucaoReagente/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                solucaoReagenteService.create(solucaoReagenteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoReagente.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/solucaoReagente/add";
            }
        }
        return "redirect:/solucaoReagentes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("solucaoReagente", solucaoReagenteService.get(id));
        return "pages/solucaoReagente/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("solucaoReagente") @Valid final SolucaoReagenteDTO solucaoReagenteDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/solucaoReagente/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                solucaoReagenteService.update(id, solucaoReagenteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoReagente.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/solucaoReagente/edit";
            }
        }
        return "redirect:/solucaoReagentes";
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
            solucaoReagenteService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("solucaoReagente.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/solucaoReagentes";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<SolucaoReagente>> revisoes = genericRevisionRepository.listaRevisoes(SolucaoReagente.class);
        model.addAttribute("audits", revisoes);
        return "pages/solucaoReagente/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        SolucaoReagente solucaoReagente = solucaoReagenteService.findById(id);
        List<EntityRevision<SolucaoReagente>> revisoes = genericRevisionRepository.listaRevisoesById(solucaoReagente.getId(), SolucaoReagente.class);
        model.addAttribute("audits", revisoes);
        return "pages/solucaoReagente/audit";
    }


}
