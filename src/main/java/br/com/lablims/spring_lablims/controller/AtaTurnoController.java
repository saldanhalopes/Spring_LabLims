package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AtaTurnoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.AtaTurnoService;
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
@RequestMapping("/ataTurnos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class AtaTurnoController {

    private final AtaTurnoService ataTurnoService;
    private final TurnoRepository turnoRepository;
    private final SetorRepository setorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipamentoRepository equipamentoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public AtaTurnoController(final AtaTurnoService ataTurnoService,
            final TurnoRepository turnoRepository, final SetorRepository setorRepository,
            final UsuarioRepository usuarioRepository,
            final EquipamentoRepository equipamentoRepository) {
        this.ataTurnoService = ataTurnoService;
        this.turnoRepository = turnoRepository;
        this.setorRepository = setorRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

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

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<AtaTurnoDTO> ataTurnos = ataTurnoService.findAll(filter, pageable);
        model.addAttribute("ataTurnos", ataTurnos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(ataTurnos));
        return "ataTurno/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("ataTurno") final AtaTurnoDTO ataTurnoDTO) {
        return "ataTurno/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("ataTurno") @Valid final AtaTurnoDTO ataTurnoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "ataTurno/add";
        }
        ataTurnoService.create(ataTurnoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("ataTurno.create.success"));
        return "redirect:/ataTurnos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("ataTurno", ataTurnoService.get(id));
        return "ataTurno/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("ataTurno") @Valid final AtaTurnoDTO ataTurnoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "ataTurno/edit";
        }
        ataTurnoService.update(id, ataTurnoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("ataTurno.update.success"));
        return "redirect:/ataTurnos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        ataTurnoService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("ataTurno.delete.success"));
        return "redirect:/ataTurnos";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<AtaTurno>> revisoes = genericRevisionRepository.listaRevisoes(AtaTurno.class);
        model.addAttribute("audits", revisoes);
        return "/ataTurno/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        AtaTurno ataTurno = ataTurnoService.findById(id);
        List<EntityRevision<AtaTurno>> revisoes = genericRevisionRepository.listaRevisoesById(ataTurno.getId(), AtaTurno.class);
        model.addAttribute("audits", revisoes);
        return "/ataTurno/audit";
    }

}
