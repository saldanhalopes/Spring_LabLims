package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.MetodologiaVersaoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.MetodologiaVersaoService;
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
@RequestMapping("/metodologiaVersaos")
public class MetodologiaVersaoController {

    private final MetodologiaVersaoService metodologiaVersaoService;
    private final MetodologiaRepository metodologiaRepository;
    private final ArquivosRepository arquivosRepository;
    private final ProdutoRepository produtoRepository;
    private final MetodologiaStatusRepository metodologiaStatusRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;


    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("metodologiaValues", metodologiaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Metodologia::getId, Metodologia::getCodigo)));
        model.addAttribute("anexoValues", arquivosRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Arquivos::getId, Arquivos::getNome)));
        model.addAttribute("produtoValues", produtoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Produto::getId, Produto::getFiscalizado)));
        model.addAttribute("statusValues", metodologiaStatusRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MetodologiaStatus::getId, MetodologiaStatus::getStatus)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<MetodologiaVersaoDTO> metodologiaVersaos = metodologiaVersaoService.findAll(filter, pageable);
        model.addAttribute("metodologiaVersaos", metodologiaVersaos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(metodologiaVersaos));
        return "parameters/metodologiaVersao/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("metodologiaVersao") final MetodologiaVersaoDTO metodologiaVersaoDTO) {
        return "parameters/metodologiaVersao/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("metodologiaVersao") @Valid final MetodologiaVersaoDTO metodologiaVersaoDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/metodologiaVersao/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                metodologiaVersaoService.create(metodologiaVersaoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("metodologiaVersao.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/metodologiaVersao/add";
            }
        }
        return "redirect:/metodologiaVersaos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("metodologiaVersao", metodologiaVersaoService.get(id));
        return "parameters/metodologiaVersao/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("metodologiaVersao") @Valid final MetodologiaVersaoDTO metodologiaVersaoDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "parameters/metodologiaVersao/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                metodologiaVersaoService.update(id, metodologiaVersaoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("metodologiaVersao.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/metodologiaVersao/edit";
            }
        }
        return "redirect:/metodologiaVersaos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = metodologiaVersaoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                metodologiaVersaoService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("metodologiaVersao.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/metodologiaVersaos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<MetodologiaVersao>> revisoes = genericRevisionRepository.listaRevisoes(MetodologiaVersao.class);
        model.addAttribute("audits", revisoes);
        return "parameters/metodologiaVersao/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        MetodologiaVersao metodologiaVersao = metodologiaVersaoService.findById(id);
        List<EntityRevision<MetodologiaVersao>> revisoes = genericRevisionRepository.listaRevisoesById(metodologiaVersao.getId(), MetodologiaVersao.class);
        model.addAttribute("audits", revisoes);
        return "parameters/metodologiaVersao/audit";
    }

}
