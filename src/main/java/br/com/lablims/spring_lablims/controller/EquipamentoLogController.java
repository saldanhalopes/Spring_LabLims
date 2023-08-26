package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EquipamentoLogDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.EquipamentoLogService;
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
@RequestMapping("/equipamentoLogs")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class EquipamentoLogController {

    private final EquipamentoLogService equipamentoLogService;
    private final EquipamentoAtividadeRepository equipamentoAtividadeRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public EquipamentoLogController(final EquipamentoLogService equipamentoLogService,
            final EquipamentoAtividadeRepository equipamentoAtividadeRepository,
            final EquipamentoRepository equipamentoRepository,
            final UsuarioRepository usuarioRepository,
            final ArquivosRepository arquivosRepository) {
        this.equipamentoLogService = equipamentoLogService;
        this.equipamentoAtividadeRepository = equipamentoAtividadeRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.arquivosRepository = arquivosRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("atividadeValues", equipamentoAtividadeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EquipamentoAtividade::getId, EquipamentoAtividade::getAtividade)));
        model.addAttribute("equipamentoValues", equipamentoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Equipamento::getId, Equipamento::getDescricao)));
        model.addAttribute("usuarioInicioValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("usuarioFimValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("anexoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<EquipamentoLogDTO> equipamentoLogs = equipamentoLogService.findAll(filter, pageable);
        model.addAttribute("equipamentoLogs", equipamentoLogs);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentoLogs));
        return "equipamentoLog/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("equipamentoLog") final EquipamentoLogDTO equipamentoLogDTO) {
        return "equipamentoLog/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("equipamentoLog") @Valid final EquipamentoLogDTO equipamentoLogDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamentoLog/add";
        }
        equipamentoLogService.create(equipamentoLogDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.create.success"));
        return "redirect:/equipamentoLogs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamentoLog", equipamentoLogService.get(id));
        return "equipamentoLog/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("equipamentoLog") @Valid final EquipamentoLogDTO equipamentoLogDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "equipamentoLog/edit";
        }
        equipamentoLogService.update(id, equipamentoLogDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.update.success"));
        return "redirect:/equipamentoLogs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        equipamentoLogService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamentoLog.delete.success"));
        return "redirect:/equipamentoLogs";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<EquipamentoLog>> revisoes = genericRevisionRepository.listaRevisoes(EquipamentoLog.class);
        model.addAttribute("audits", revisoes);
        return "/equipamentoLog/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        EquipamentoLog equipamentoLog = equipamentoLogService.findById(id);
        List<EntityRevision<EquipamentoLog>> revisoes = genericRevisionRepository.listaRevisoesById(equipamentoLog.getId(), EquipamentoLog.class);
        model.addAttribute("audits", revisoes);
        return "/equipamentoLog/audit";
    }

}
