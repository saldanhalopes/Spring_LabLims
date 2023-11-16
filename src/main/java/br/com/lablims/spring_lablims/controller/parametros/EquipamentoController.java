package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EquipamentoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.ArquivosService;
import br.com.lablims.spring_lablims.service.EquipamentoService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;
    private final EquipamentoTipoRepository equipamentoTipoRepository;
    private final SetorRepository setorRepository;
    private final ArquivosRepository arquivosRepository;
    private final GrandezaRepository grandezaRepository;
    private final ArquivosService arquivosService;
    @Autowired
    private GenericRevisionRepository genericRevisionRepository;
    @Autowired
    private UsuarioService usuarioService;

    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) {
        // Convert multipart object to byte[]
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
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
        model.addAttribute("grandezaValues", grandezaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Grandeza::getId, Grandeza::getGrandeza)));
    }


    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 30) final Pageable pageable,
                       final Model model) {
        final SimplePage<EquipamentoDTO> equipamentos = equipamentoService.findAll(filter, pageable);
        model.addAttribute("equipamentos", equipamentos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(equipamentos));
        return "parameters/equipamento/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("equipamento") final EquipamentoDTO equipamentoDTO) {
        return "parameters/equipamento/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("equipamento") @Valid final EquipamentoDTO equipamentoDTO, final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/equipamento/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                equipamentoService.create(equipamentoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamento.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/equipamento/add";
            }
        }
        return "redirect:/equipamentos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamento", equipamentoService.get(id));
        return "parameters/equipamento/edit";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        model.addAttribute("equipamento", equipamentoService.get(id));
        return "parameters/equipamento/details";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("equipamento") @Valid final EquipamentoDTO equipamentoDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) throws IOException {
        if (bindingResult.hasErrors()) {
            return "parameters/equipamento/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                equipamentoService.update(id, equipamentoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("equipamento.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/equipamento/edit";
            }
        }
        return "redirect:/equipamentos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @GetMapping("/upload/{id}")
    public String uploads(@PathVariable final Integer id,
                          @ModelAttribute("arquivos") final Arquivos arquivos,
                          final Model model) {
        final List<Arquivos> arquivoss = equipamentoService.findArquivosByEquipamento(id);
        model.addAttribute("equipamento", equipamentoService.get(id));
        model.addAttribute("arquivoss", arquivoss);
        return "parameters/equipamento/upload";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/upload/{id}")
    public String upload(@PathVariable final Integer id,
                         @RequestParam("arquivo") MultipartFile file, final Model model,
                         final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                         Principal principal, @ModelAttribute("password") String pass) throws IOException {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            Equipamento equipamento = equipamentoService.findEquipamentoWithArquivos(id);
            Arquivos arquivos = new Arquivos();
            arquivos.setArquivo(file.getBytes());
            arquivos.setTamanho(file.getSize());
            arquivos.setNome(file.getOriginalFilename());
            arquivos.setTipo(file.getContentType());
            arquivos.setDescricao(motivo);
            arquivosRepository.save(arquivos);
            equipamento.getArquivos().add(arquivos);
            equipamentoService.update(equipamento);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("arquivos.update.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "redirect:/equipamentos/upload/{id}";
        }
        return "redirect:/equipamentos/upload/{id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = equipamentoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                equipamentoService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("equipamento.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/equipamentos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/{eqId}/arquivo/delete/{id}")
    public String deleteArquivo(@PathVariable final Integer eqId,
                                @PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            arquivosService.deleteEquipamento(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("arquivos.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/equipamentos/upload/{eqId}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Equipamento>> revisoes = genericRevisionRepository.listaRevisoes(Equipamento.class);
        model.addAttribute("audits", revisoes);
        return "parameters/equipamento/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Equipamento equipamento = equipamentoService.findById(id);
        List<EntityRevision<Equipamento>> revisoes = genericRevisionRepository.listaRevisoesById(equipamento.getId(), Equipamento.class);
        model.addAttribute("audits", revisoes);
        return "parameters/equipamento/audit";
    }

    @GetMapping("/details/imagem/{id}")
    @ResponseBody
    public void showImage(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        response.getOutputStream().write(equipamentoService.get(id).getImagem());
        response.getOutputStream().close();
    }

}
