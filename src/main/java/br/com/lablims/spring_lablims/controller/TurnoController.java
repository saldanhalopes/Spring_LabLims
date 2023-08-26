package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Turno;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.TurnoDTO;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.TurnoService;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/turnos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class TurnoController {

    private final TurnoService turnoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public TurnoController(final TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<TurnoDTO> turnos = turnoService.findAll(filter, pageable);
        model.addAttribute("turnos", turnos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(turnos));
        return "turno/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("turno") final TurnoDTO turnoDTO) {
        return "turno/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("turno") @Valid final TurnoDTO turnoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "turno/add";
        }
        turnoService.create(turnoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("turno.create.success"));
        return "redirect:/turnos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("turno", turnoService.get(id));
        return "turno/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("turno") @Valid final TurnoDTO turnoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "turno/edit";
        }
        turnoService.update(id, turnoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("turno.update.success"));
        return "redirect:/turnos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = turnoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            turnoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("turno.delete.success"));
        }
        return "redirect:/turnos";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Turno>> revisoes = genericRevisionRepository.listaRevisoes(Turno.class);
        model.addAttribute("audits", revisoes);
        return "/turno/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Turno turno = turnoService.findById(id);
        List<EntityRevision<Turno>> revisoes = genericRevisionRepository.listaRevisoesById(turno.getId(), Turno.class);
        model.addAttribute("audits", revisoes);
        return "/turno/audit";
    }

}
