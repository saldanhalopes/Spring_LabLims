package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EquipamentoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.EquipamentoService;
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
@RequestMapping("/equipamentos")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;
    private final EquipamentoTipoRepository equipamentoTipoRepository;
    private final SetorRepository setorRepository;
    private final ArquivosRepository arquivosRepository;
    private final EscalaMedidaRepository escalaMedidaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public EquipamentoController(final EquipamentoService equipamentoService,
            final EquipamentoTipoRepository equipamentoTipoRepository,
            final SetorRepository setorRepository, final ArquivosRepository arquivosRepository,
            final EscalaMedidaRepository escalaMedidaRepository) {
        this.equipamentoService = equipamentoService;
        this.equipamentoTipoRepository = equipamentoTipoRepository;
        this.setorRepository = setorRepository;
        this.arquivosRepository = arquivosRepository;
        this.escalaMedidaRepository = escalaMedidaRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("tipoValues", equipamentoTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EquipamentoTipo::getId, EquipamentoTipo::getTipo)));
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
        model.addAttribute("certificadoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
        model.addAttribute("manualValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
        model.addAttribute("procedimentoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
        model.addAttribute("escalaValues", escalaMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EscalaMedida::getId, EscalaMedida::getEscala)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<EquipamentoDTO> equipamentos = equipamentoService.findAll(filter, pageable);
        model.addAttribute("equipamentos", equipamentos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentos));
        return "equipamento/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("equipamento") final EquipamentoDTO equipamentoDTO) {
        return "equipamento/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("equipamento") @Valid final EquipamentoDTO equipamentoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamento/add";
        }
        equipamentoService.create(equipamentoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamento.create.success"));
        return "redirect:/equipamentos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamento", equipamentoService.get(id));
        return "equipamento/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("equipamento") @Valid final EquipamentoDTO equipamentoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamento/edit";
        }
        equipamentoService.update(id, equipamentoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamento.update.success"));
        return "redirect:/equipamentos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = equipamentoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            equipamentoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamento.delete.success"));
        }
        return "redirect:/equipamentos";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Equipamento>> revisoes = genericRevisionRepository.listaRevisoes(Equipamento.class);
        model.addAttribute("audits", revisoes);
        return "/equipamento/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Equipamento equipamento = equipamentoService.findById(id);
        List<EntityRevision<Equipamento>> revisoes = genericRevisionRepository.listaRevisoesById(equipamento.getId(), Equipamento.class);
        model.addAttribute("audits", revisoes);
        return "/equipamento/audit";
    }

}
