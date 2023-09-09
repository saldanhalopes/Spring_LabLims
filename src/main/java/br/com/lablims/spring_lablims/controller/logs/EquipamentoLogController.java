package br.com.lablims.spring_lablims.controller.logs;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EquipamentoDTO;
import br.com.lablims.spring_lablims.model.EquipamentoLogDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.EquipamentoLogService;
import br.com.lablims.spring_lablims.service.EquipamentoService;
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
@RequestMapping("/equipamentoLogs")
public class EquipamentoLogController {

    private final EquipamentoLogService equipamentoLogService;
    private final EquipamentoService equipamentoService;
    private final EquipamentoAtividadeRepository equipamentoAtividadeRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public EquipamentoLogController(final EquipamentoLogService equipamentoLogService,
                                    EquipamentoService equipamentoService, final EquipamentoAtividadeRepository equipamentoAtividadeRepository,
                                    final EquipamentoRepository equipamentoRepository,
                                    final UsuarioRepository usuarioRepository,
                                    final ArquivosRepository arquivosRepository) {
        this.equipamentoLogService = equipamentoLogService;
        this.equipamentoService = equipamentoService;
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
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getUsername)));
        model.addAttribute("usuarioFimValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getUsername)));
        model.addAttribute("anexoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String blocks(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<EquipamentoDTO> equipamentos = equipamentoService.findAll(filter, pageable);
        model.addAttribute("equipamentos", equipamentos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentos));
        return "logs/equipamentoLog/blocks";
    }

    @GetMapping("/list/{id}")
    public String list(@PathVariable final Integer id,
                       @PageableDefault(size = 1) final Pageable pageable, final Model model) {
        final SimplePage<EquipamentoLogDTO> equipamentoLogs = equipamentoLogService.findAll(id.toString(), pageable);
        model.addAttribute("equipamentoLogs", equipamentoLogs);
        return "logs/equipamentoLog/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("equipamentoLog") final EquipamentoLogDTO equipamentoLogDTO) {
        return "logs/equipamentoLog/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("equipamentoLog") @Valid final EquipamentoLogDTO equipamentoLogDTO, final Model model,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "logs/equipamentoLog/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                equipamentoLogService.create(equipamentoLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "logs/equipamentoLog/add";
            }
        }
        return "redirect:/equipamentoLogs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamentoLog", equipamentoLogService.get(id));
        return "logs/equipamentoLog/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("equipamentoLog") @Valid final EquipamentoLogDTO equipamentoLogDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "logs/equipamentoLog/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                equipamentoLogService.update(id, equipamentoLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "logs/equipamentoLog/edit";
            }
        }
        return "redirect:/equipamentoLogs";
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
            equipamentoLogService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamentoLog.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
        }
        return "redirect:/equipamentoLogs";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<EquipamentoLog>> revisoes = genericRevisionRepository.listaRevisoes(EquipamentoLog.class);
        model.addAttribute("audits", revisoes);
        return "logs/equipamentoLog/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        EquipamentoLog equipamentoLog = equipamentoLogService.findById(id);
        List<EntityRevision<EquipamentoLog>> revisoes = genericRevisionRepository.listaRevisoesById(equipamentoLog.getId(), EquipamentoLog.class);
        model.addAttribute("audits", revisoes);
        return "logs/equipamentoLog/audit";
    }

}
