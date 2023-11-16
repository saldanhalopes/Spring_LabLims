package br.com.lablims.spring_lablims.controller.logs;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AtaTurnoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.AtaTurnoService;
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
@RequestMapping("/ataTurnos")
public class AtaTurnoController {

    private final AtaTurnoService ataTurnoService;
    private final TurnoRepository turnoRepository;
    private final SetorRepository setorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipamentoRepository equipamentoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("turnoValues", turnoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Turno::getId, Turno::getTurno)));
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
        model.addAttribute("usuarioValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("equipamentosValues", equipamentoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Equipamento::getId, Equipamento::getDescricao)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<AtaTurnoDTO> ataTurnos = ataTurnoService.findAll(filter, pageable);
        model.addAttribute("ataTurnos", ataTurnos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(ataTurnos));
        return "pages/ataTurno/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("ataTurno") final AtaTurnoDTO ataTurnoDTO) {
        return "pages/ataTurno/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("ataTurno") @Valid final AtaTurnoDTO ataTurnoDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/ataTurno/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                ataTurnoService.create(ataTurnoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("ataTurno.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/ataTurno/add";
            }
        }
        return "redirect:/ataTurnos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("ataTurno", ataTurnoService.get(id));
        return "pages/ataTurno/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("ataTurno") @Valid final AtaTurnoDTO ataTurnoDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/ataTurno/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                ataTurnoService.update(id, ataTurnoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("ataTurno.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/ataTurno/edit";
            }
        }
        return "redirect:/ataTurnos";
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
            ataTurnoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("ataTurno.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/ataTurnos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<AtaTurno>> revisoes = genericRevisionRepository.listaRevisoes(AtaTurno.class);
        model.addAttribute("audits", revisoes);
        return "pages/ataTurno/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        AtaTurno ataTurno = ataTurnoService.findById(id);
        List<EntityRevision<AtaTurno>> revisoes = genericRevisionRepository.listaRevisoesById(ataTurno.getId(), AtaTurno.class);
        model.addAttribute("audits", revisoes);
        return "pages/ataTurno/audit";
    }

}
