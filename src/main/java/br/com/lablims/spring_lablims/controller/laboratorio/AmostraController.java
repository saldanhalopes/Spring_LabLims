package br.com.lablims.spring_lablims.controller.laboratorio;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AmostraDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.AmostraService;
import br.com.lablims.spring_lablims.service.UsuarioService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/amostras")
public class AmostraController {

    private final AmostraService amostraService;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoteRepository loteRepository;
    private final AmostraTipoRepository amostraTipoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

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
        model.addAttribute("usuariolValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getUsername)));
        model.addAttribute("loteValues", loteRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Lote::getId, Lote::getLote)));
        model.addAttribute("amostraTipoValues", amostraTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AmostraTipo::getId, AmostraTipo::getTipo)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<AmostraDTO> amostras = amostraService.findAllOfAmostras(pageable);
        model.addAttribute("amostras", amostras);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(amostras));
        return "laboratorio/amostra/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("amostra") final AmostraDTO amostraDTO) {
        return "laboratorio/amostra/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("amostra") @Valid final AmostraDTO amostraDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (!bindingResult.hasFieldErrors("codigoAmostra") && amostraDTO.getCodigoAmostra() != null && amostraService.codigoAmostraExists(amostraDTO.getCodigoAmostra())) {
            bindingResult.rejectValue("codigoAmostra", "Exists.exists");
        }
        if (bindingResult.hasErrors()) {
            return "laboratorio/amostra/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                amostraDTO.setResponsavel(principal.getName());
                amostraService.create(amostraDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("amostra.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "laboratorio/amostra/add";
            }
        }
        return "redirect:/amostras";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        model.addAttribute("amostra", amostraService.get(id));
        return "laboratorio/amostra/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("amostra", amostraService.get(id));
        return "laboratorio/amostra/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("amostra") @Valid final AmostraDTO amostraDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "laboratorio/amostra/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                amostraService.update(id, amostraDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("amostra.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "laboratorio/amostra/edit";
            }
        }
        return "redirect:/amostras";
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
            amostraService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("amostra.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/amostras";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Amostra>> revisoes = genericRevisionRepository.listaRevisoes(Amostra.class);
        model.addAttribute("audits", revisoes);
        return "laboratorio/amostra/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Amostra amostra = amostraService.findById(id);
        List<EntityRevision<Amostra>> revisoes = genericRevisionRepository.listaRevisoesById(amostra.getId(), Amostra.class);
        model.addAttribute("audits", revisoes);
        return "laboratorio/amostra/audit";
    }

}
