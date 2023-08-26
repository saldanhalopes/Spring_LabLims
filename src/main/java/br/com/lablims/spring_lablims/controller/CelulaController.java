package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Celula;
import br.com.lablims.spring_lablims.domain.CelulaTipo;
import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.CelulaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CelulaTipoRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.service.CelulaService;
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
@RequestMapping("/celulas")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class CelulaController {

    private final CelulaService celulaService;
    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CelulaTipoRepository celulaTipoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public CelulaController(final CelulaService celulaService,
            final EquipamentoRepository equipamentoRepository,
            final UsuarioRepository usuarioRepository,
            final CelulaTipoRepository celulaTipoRepository) {
        this.celulaService = celulaService;
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.celulaTipoRepository = celulaTipoRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("equipamentoValues", equipamentoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Equipamento::getId, Equipamento::getDescricao)));
        model.addAttribute("usuarioValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("tipoValues", celulaTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(CelulaTipo::getId, CelulaTipo::getTipo)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<CelulaDTO> celulas = celulaService.findAll(filter, pageable);
        model.addAttribute("celulas", celulas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(celulas));
        return "celula/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("celula") final CelulaDTO celulaDTO) {
        return "celula/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("celula") @Valid final CelulaDTO celulaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "celula/add";
        }
        celulaService.create(celulaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("celula.create.success"));
        return "redirect:/celulas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("celula", celulaService.get(id));
        return "celula/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("celula") @Valid final CelulaDTO celulaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "celula/edit";
        }
        celulaService.update(id, celulaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("celula.update.success"));
        return "redirect:/celulas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = celulaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            celulaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("celula.delete.success"));
        }
        return "redirect:/celulas";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Celula>> revisoes = genericRevisionRepository.listaRevisoes(Celula.class);
        model.addAttribute("audits", revisoes);
        return "/celula/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Celula celula = celulaService.findById(id);
        List<EntityRevision<Celula>> revisoes = genericRevisionRepository.listaRevisoesById(celula.getId(), Celula.class);
        model.addAttribute("audits", revisoes);
        return "/celula/audit";
    }

}
