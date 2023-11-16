package br.com.lablims.spring_lablims.controller.logs;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AmostraDTO;
import br.com.lablims.spring_lablims.model.EquipamentoDTO;
import br.com.lablims.spring_lablims.model.EquipamentoLogDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.*;
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
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/equipamentoLogs")
public class EquipamentoLogController {

    private final EquipamentoLogService equipamentoLogService;
    private final EquipamentoService equipamentoService;
    private final EquipamentoAtividadeRepository equipamentoAtividadeRepository;
    private final ArquivosRepository arquivosRepository;
    private final ArquivosService arquivosService;
    private final AmostraRepository amostraRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("atividadeValues", equipamentoAtividadeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EquipamentoAtividade::getId, EquipamentoAtividade::getAtividade)));
        model.addAttribute("codigoAmostraValues", amostraRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Amostra::getId, Amostra::getCodigoAmostra)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String blocks(@RequestParam(required = false) final String filter,
                         @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                         final Model model) {
        final SimplePage<EquipamentoDTO> equipamentos = equipamentoService.findAllOfEquipamentos(pageable);
        model.addAttribute("equipamentos", equipamentos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentos));
        return "logs/equipamentoLog/blocks";
    }

    @GetMapping("/list/{equip_id}")
    public String list(@PathVariable final Integer equip_id,
                       @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 50) final Pageable pageable,
                       final Model model) {
        final SimplePage<EquipamentoLogDTO> equipamentoLogs = equipamentoLogService.findAllByEquipamento(equip_id, pageable);
        final EquipamentoDTO equipamento = equipamentoService.get(equip_id);
        model.addAttribute("equipamentoLogs", equipamentoLogs);
        model.addAttribute("equipamento", equipamento);
        return "logs/equipamentoLog/list";
    }

    @GetMapping("/add/{equip_id}")
    public String add(@PathVariable final Integer equip_id, final Model model,
                      @ModelAttribute("equipamentoLog") final EquipamentoLogDTO equipamentoLogDTO) {
        model.addAttribute("equipamento", equipamentoService.get(equip_id));
        return "logs/equipamentoLog/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add/{equip_id}")
    public String add(@ModelAttribute("equipamentoLog") @Valid final EquipamentoLogDTO equipamentoLogDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes, @PathVariable final Integer equip_id,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "logs/equipamentoLog/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                equipamentoLogDTO.setUsuarioInicio(usuarioService.findByUsername(principal.getName()).getId());
                equipamentoLogDTO.setDataInicio(LocalDateTime.now());
                equipamentoLogDTO.setEquipamento(equip_id);
                equipamentoLogService.create(equipamentoLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "logs/equipamentoLog/add";
            }
        }
        return "redirect:/equipamentoLogs/list/{equip_id}";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        EquipamentoLogDTO equipamentoLogDTO = equipamentoLogService.get(id);
        model.addAttribute("equipamentoLog", equipamentoLogDTO);
        final EquipamentoDTO equipamento = equipamentoService.get(equipamentoLogDTO.getEquipamento());
        model.addAttribute("equipamento", equipamento);
        return "logs/equipamentoLog/details";
    }

    @GetMapping("/edit/{equip_id}/{id}")
    public String edit(@PathVariable final Integer id, @PathVariable final Integer equip_id, final Model model) {
        EquipamentoLogDTO equipamentoLogDTO = equipamentoLogService.get(id);
        model.addAttribute("equipamentoLog", equipamentoLogDTO);
        final EquipamentoDTO equipamento = equipamentoService.get(equip_id);
        model.addAttribute("equipamento", equipamento);
        return "logs/equipamentoLog/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{equip_id}/{id}")
    public String edit(@ModelAttribute("equipamentoLog") @Valid final EquipamentoLogDTO equipamentoLogDTO,
                       final BindingResult bindingResult, final Model model,
                       @PathVariable final Integer id,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "logs/equipamentoLog/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                equipamentoLogService.updateLog(id, equipamentoLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.update.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "redirect:/equipamentoLogs/edit/{equip_id}/{id}";
            }
        }
        return "redirect:/equipamentoLogs/list/{equip_id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/{equip_id}/finalizar/{id}")
    public String finalizaRegistro(@PathVariable final Integer id, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("dataFim") LocalDateTime dataFim,
                      @ModelAttribute("password") String pass) {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                EquipamentoLogDTO equipamentoLogDTO = equipamentoLogService.get(id);
                CustomRevisionEntity.setMotivoText("Finaliza Registro");
                equipamentoLogDTO.setUsuarioFim(usuarioService.findByUsername(principal.getName()).getId());
                equipamentoLogDTO.setDataFim(dataFim);
                equipamentoLogService.update(id, equipamentoLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamentoLog.create.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "redirect:/equipamentoLogs/list/{equip_id}";
        }
        return "redirect:/equipamentoLogs/list/{equip_id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @GetMapping("/upload/{equip_id}/{id}")
    public String uploads(@PathVariable final Integer id, @PathVariable final Integer equip_id,
                          @ModelAttribute("arquivos") final Arquivos arquivos,
                          final Model model) {
        model.addAttribute("equipamentoLog", equipamentoLogService.get(id));
        model.addAttribute("equipamento", equipamentoService.get(equip_id));
        model.addAttribute("arquivoss", equipamentoLogService.findArquivosByEquipamentoLog(id));
        return "logs/equipamentoLog/upload";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/upload/{equip_id}/{id}")
    public String upload(@PathVariable final Integer id,
                         @RequestParam("arquivo") MultipartFile file, final Model model,
                         final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                         Principal principal, @ModelAttribute("password") String pass) throws IOException {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            EquipamentoLog equipamentoLog = equipamentoLogService.findEquipamentoLogWithArquivos(id);
            Arquivos arquivos = new Arquivos();
            arquivos.setArquivo(file.getBytes());
            arquivos.setTamanho(file.getSize());
            arquivos.setNome(file.getOriginalFilename());
            arquivos.setTipo(file.getContentType());
            arquivos.setDescricao(motivo);
            arquivosRepository.save(arquivos);
            equipamentoLog.getArquivos().add(arquivos);
            equipamentoLogService.update(equipamentoLog);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("arquivos.update.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "redirect:/equipamentoLogs/upload/{equip_id}/{id}";
        }
        return "redirect:/equipamentoLogs/upload/{equip_id}/{id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/{equip_id}/{log_id}/arquivo/delete/{id}")
    public String deleteArquivo(@PathVariable final Integer id,
                                final RedirectAttributes redirectAttributes,
                                @ModelAttribute("motivo") String motivo,
                                Principal principal,
                                @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            arquivosService.deleteEquipamentoLog(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("arquivos.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/equipamentoLogs/upload/{equip_id}/{log_id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/{equip_id}/delete/{id}")
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
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/equipamentoLogs/list/{equip_id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/lock/{equip_id}/{id}")
    public String lock(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            equipamentoLogService.lock(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamentoLog.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/equipamentoLogs/list/{equip_id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/unlock/{equip_id}/{id}")
    public String unlock(@PathVariable final Integer id,
                       final RedirectAttributes redirectAttributes,
                       @ModelAttribute("motivo") String motivo,
                       Principal principal,
                       @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            equipamentoLogService.unlock(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamentoLog.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/equipamentoLogs/list/{equip_id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{equip_id}")
    public String getRevisions(Model model, @PathVariable final Integer equip_id) {
        List<EntityRevision<EquipamentoLog>> revisoes = genericRevisionRepository.listaRevisoes(EquipamentoLog.class);
        final EquipamentoDTO equipamento = equipamentoService.get(equip_id);
        model.addAttribute("equipamento", equipamento);
        model.addAttribute("audits", revisoes);
        return "logs/equipamentoLog/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{equip_id}/{id}")
    public String getRevisions(Model model, @PathVariable final Integer equip_id, @PathVariable final Integer id) {
        EquipamentoLog equipamentoLog = equipamentoLogService.findById(id);
        List<EntityRevision<EquipamentoLog>> revisoes = genericRevisionRepository.listaRevisoesById(equipamentoLog.getId(), EquipamentoLog.class);
        model.addAttribute("equipamentoLog", equipamentoLog);
        final EquipamentoDTO equipamento = equipamentoService.get(equip_id);
        model.addAttribute("equipamento", equipamento);
        model.addAttribute("audits", revisoes);
        return "logs/equipamentoLog/audit";
    }


    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @GetMapping("/amostra/{equip_id}/{id}")
    public String amostras(@PathVariable final Integer id, @PathVariable final Integer equip_id,
                          @ModelAttribute("amostra") final Amostra amostra,
                          final Model model) {
        model.addAttribute("equipamentoLog", equipamentoLogService.get(id));
        model.addAttribute("equipamento", equipamentoService.get(equip_id));
        model.addAttribute("amostras", equipamentoLogService.findAmostrasByEquipamentoLog(id));
        return "logs/equipamentoLog/amostra";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/amostra/{equip_id}/{id}")
    public String addAmostras(@ModelAttribute("amostra") final Amostra amostra,
                              @PathVariable final Integer id, final Model model,
                         final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                         Principal principal, @ModelAttribute("password") String pass) throws IOException {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            if(equipamentoLogService.amostraExists(amostra.getId(), id)){
                equipamentoLogService.updateAmostra(amostra.getId(), id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("amostra.update.success"));
            }else{
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("amostra.is.exists"));
                return "redirect:/equipamentoLogs/amostra/{equip_id}/{id}";
            }
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "redirect:/equipamentoLogs/amostra/{equip_id}/{id}";
        }
        return "redirect:/equipamentoLogs/amostra/{equip_id}/{id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/{equip_id}/{log_id}/amostra/delete/{id}")
    public String deleteAmostra(@PathVariable final Integer id,
                                final RedirectAttributes redirectAttributes,
                                @ModelAttribute("amostra") final Amostra amostra,
                                @ModelAttribute("motivo") String motivo,
                                Principal principal,
                                @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            equipamentoLogService.deleteAmostra(amostra.getId(), id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("amostra.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/equipamentoLogs/amostra/{equip_id}/{log_id}";
    }


}
