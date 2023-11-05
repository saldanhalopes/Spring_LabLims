package br.com.lablims.spring_lablims.controller.laboratorio;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.ArquivosDTO;
import br.com.lablims.spring_lablims.model.LoteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.ArquivosService;
import br.com.lablims.spring_lablims.service.LoteService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService loteService;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final MaterialRepository materialRepository;
    private final ClienteRepository clienteRepository;
    private final ArquivosService arquivosService;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public LoteController(final LoteService loteService,
                          final UnidadeMedidaRepository unidadeMedidaRepository,
                          final MaterialRepository materialRepository,
                          final ClienteRepository clienteRepository,
                          final ArquivosService arquivosService,
                          final ArquivosRepository arquivosRepository) {
        this.loteService = loteService;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.materialRepository = materialRepository;
        this.clienteRepository = clienteRepository;
        this.arquivosService = arquivosService;
        this.arquivosRepository = arquivosRepository;
    }

    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) {
        // Convert multipart object to byte[]
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
        model.addAttribute("materialValues", materialRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Material::getId, Material::getFiscalizado)));
        model.addAttribute("clienteValues", clienteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Cliente::getId, Cliente::getCliente)));
        model.addAttribute("arquivosValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@SortDefault(sort = "id") @PageableDefault(size = 100) final Pageable pageable,
                       final Model model) {
        final SimplePage<LoteDTO> lotes = loteService.findAllOfLotes(pageable);
        model.addAttribute("lotes", lotes);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(lotes));
        return "laboratorio/lote/list";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @GetMapping("/add")
    public String add(@ModelAttribute("lote") final LoteDTO loteDTO) {
        return "laboratorio/lote/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("lote") @Valid final LoteDTO loteDTO,
                      final BindingResult bindingResult, final Model model,
                      final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (!bindingResult.hasFieldErrors("lote") && loteDTO.getLote() != null && loteService.loteExists(loteDTO.getLote())) {
            bindingResult.rejectValue("lote", "Exists.exists");
        }
        if (bindingResult.hasErrors()) {
            return "laboratorio/lote/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                loteService.create(loteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lote.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "laboratorio/lote/add";
            }
        }
        return "redirect:/lotes";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        model.addAttribute("lote", loteService.get(id));
        return "laboratorio/lote/details";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("lote", loteService.get(id));
        return "laboratorio/lote/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("lote") @Valid final LoteDTO loteDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "laboratorio/lote/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                loteService.update(id, loteDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lote.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "laboratorio/lote/edit";
            }
        }
        return "redirect:/lotes";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @GetMapping("/upload/{id}")
    public String uploads(@PathVariable final Integer id,
                          @ModelAttribute("arquivos") final Arquivos arquivos,
                          final Model model) {
        final List<Arquivos> arquivoss = loteService.findArquivosByLote(id);
        model.addAttribute("lote", loteService.get(id));
        model.addAttribute("arquivoss", arquivoss);
        return "laboratorio/lote/upload";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/upload/{id}")
    public String upload(@PathVariable final Integer id,
                         @RequestParam("arquivo") MultipartFile file, final Model model,
                         final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                         Principal principal, @ModelAttribute("password") String pass) throws IOException {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            Lote lote = loteService.findLoteWithArquivos(id);
            Arquivos arquivos = new Arquivos();
            arquivos.setArquivo(file.getBytes());
            arquivos.setTamanho(file.getSize());
            arquivos.setNome(file.getOriginalFilename());
            arquivos.setTipo(file.getContentType());
            arquivos.setDescricao(motivo);
            arquivosRepository.save(arquivos);
            lote.getArquivos().add(arquivos);
            loteService.updateArquivo(lote);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("arquivos.update.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "redirect:/lotes/upload/{id}";
        }
        return "redirect:/lotes/upload/{id}";
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
            loteService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("lote.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/lotes";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/{lotId}/arquivo/delete/{id}")
    public String deleteArquivo(@PathVariable final Integer lotId,
                                @PathVariable final Integer id,
                                final RedirectAttributes redirectAttributes,
                                @ModelAttribute("motivo") String motivo,
                                Principal principal,
                                @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            arquivosService.deleteLote(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("arquivos.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/lotes/upload/{lotId}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Lote>> revisoes = genericRevisionRepository.listaRevisoes(Lote.class);
        model.addAttribute("audits", revisoes);
        return "laboratorio/lote/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Lote lote = loteService.findById(id);
        List<EntityRevision<Lote>> revisoes = genericRevisionRepository.listaRevisoesById(lote.getId(), Lote.class);
        model.addAttribute("audits", revisoes);
        return "laboratorio/lote/audit";
    }

}
