package br.com.lablims.spring_lablims.controller.colunas;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.ColunaUtilDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.ColunaUtilService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/colunaUtils")
public class ColunaUtilController {

    private final ColunaUtilService colunaUtilService;
    private final ColunaRepository colunaRepository;
    private final SetorRepository setorRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final AnaliseRepository analiseRepository;
    private final StorageEnderecoRepository storageEnderecoRepository;
    private final ArquivosRepository arquivosRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("colunaValues", colunaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Coluna::getId, Coluna::getCodigo)));
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
        model.addAttribute("metodologiaVersaoValues", metodologiaVersaoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MetodologiaVersao::getId, MetodologiaVersao::getObs)));
        model.addAttribute("analiseValues", analiseRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Analise::getId, Analise::getAnalise)));
        model.addAttribute("colunaVagaValues", storageEnderecoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(StorageEndereco::getId, StorageEndereco::getObs)));
        model.addAttribute("certificadoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
        model.addAttribute("anexosValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<ColunaUtilDTO> colunaUtils = colunaUtilService.findAll(filter, pageable);
        model.addAttribute("colunaUtils", colunaUtils);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(colunaUtils));
        return "pages/colunaUtil/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("colunaUtil") final ColunaUtilDTO colunaUtilDTO) {
        return "pages/colunaUtil/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("colunaUtil") @Valid final ColunaUtilDTO colunaUtilDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/colunaUtil/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                colunaUtilService.create(colunaUtilDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("colunaUtil.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/colunaUtil/add";
            }
        }
        return "redirect:/colunaUtils";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("colunaUtil", colunaUtilService.get(id));
        return "pages/colunaUtil/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("colunaUtil") @Valid final ColunaUtilDTO colunaUtilDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/colunaUtil/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaUtilService.update(id, colunaUtilDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("colunaUtil.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/colunaUtil/edit";
            }
        }
        return "redirect:/colunaUtils";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = colunaUtilService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaUtilService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("colunaUtil.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/colunaUtils";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<ColunaUtil>> revisoes = genericRevisionRepository.listaRevisoes(ColunaUtil.class);
        model.addAttribute("audits", revisoes);
        return "pages/colunaUtil/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        ColunaUtil colunaUtil = colunaUtilService.findById(id);
        List<EntityRevision<ColunaUtil>> revisoes = genericRevisionRepository.listaRevisoesById(colunaUtil.getId(), ColunaUtil.class);
        model.addAttribute("audits", revisoes);
        return "pages/colunaUtil/audit";
    }
}
