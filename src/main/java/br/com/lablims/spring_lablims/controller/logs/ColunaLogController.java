package br.com.lablims.spring_lablims.controller.logs;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.ColunaLogDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.ColunaLogService;
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
@RequestMapping("/colunaLogs")
public class ColunaLogController {

    private final ColunaLogService colunaLogService;
    private final ColunaUtilRepository colunaUtilRepository;
    private final UsuarioRepository usuarioRepository;
    private final CampanhaRepository campanhaRepository;
    private final AnaliseRepository analiseRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public ColunaLogController(final ColunaLogService colunaLogService,
                               final ColunaUtilRepository colunaUtilRepository,
                               final UsuarioRepository usuarioRepository, final CampanhaRepository campanhaRepository,
                               final AnaliseRepository analiseRepository,
                               final EquipamentoRepository equipamentoRepository,
                               final ArquivosRepository arquivosRepository) {
        this.colunaLogService = colunaLogService;
        this.colunaUtilRepository = colunaUtilRepository;
        this.usuarioRepository = usuarioRepository;
        this.campanhaRepository = campanhaRepository;
        this.analiseRepository = analiseRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.arquivosRepository = arquivosRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("colunaUtilValues", colunaUtilRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ColunaUtil::getId, ColunaUtil::getCodigoColuna)));
        model.addAttribute("usuarioInicioValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("usuarioFimValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getCep)));
        model.addAttribute("campanhaValues", campanhaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Campanha::getId, Campanha::getStatus)));
        model.addAttribute("analiseValues", analiseRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Analise::getId, Analise::getAnalise)));
        model.addAttribute("equipamentoValues", equipamentoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Equipamento::getId, Equipamento::getDescricao)));
        model.addAttribute("anexoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<ColunaLogDTO> colunaLogs = colunaLogService.findAll(filter, pageable);
        model.addAttribute("colunaLogs", colunaLogs);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(colunaLogs));
        return "logs/colunaLog/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("colunaLog") final ColunaLogDTO colunaLogDTO) {
        return "logs/colunaLog/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("colunaLog") @Valid final ColunaLogDTO colunaLogDTO, final Model model,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "logs/colunaLog/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                colunaLogService.create(colunaLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("colunaLog.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "logs/colunaLog/add";
            }
        }
        return "redirect:/colunaLogs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("colunaLog", colunaLogService.get(id));
        return "logs/colunaLog/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("colunaLog") @Valid final ColunaLogDTO colunaLogDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "logs/colunaLog/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaLogService.update(id, colunaLogDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("colunaLog.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "logs/colunaLog/edit";
            }
        }
        return "redirect:/colunaLogs";
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
            colunaLogService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("colunaLog.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
        }
        return "redirect:/colunaLogs";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<ColunaLog>> revisoes = genericRevisionRepository.listaRevisoes(ColunaLog.class);
        model.addAttribute("audits", revisoes);
        return "logs/colunaLog/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        ColunaLog colunaLog = colunaLogService.findById(id);
        List<EntityRevision<ColunaLog>> revisoes = genericRevisionRepository.listaRevisoesById(colunaLog.getId(), ColunaLog.class);
        model.addAttribute("audits", revisoes);
        return "logs/colunaLog/audit";
    }
}
