package br.com.lablims.spring_lablims.controller.colunas;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.Atributo;
import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.model.ColunaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AtributoRepository;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.ColunaService;
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
@RequestMapping("/colunas")
public class ColunaController {

    private final ColunaService colunaService;
    private final AtributoRepository atributoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
//        model.addAttribute("tipoColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(br.com.lablims.spring_lablims.domain.Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
//        model.addAttribute("fabricanteColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
//        model.addAttribute("marcaColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(br.com.lablims.spring_lablims.domain.Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
//        model.addAttribute("faseColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(br.com.lablims.spring_lablims.domain.Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
//        model.addAttribute("tamanhoColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(br.com.lablims.spring_lablims.domain.Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
//        model.addAttribute("diametroColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(br.com.lablims.spring_lablims.domain.Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
//        model.addAttribute("particulaColunaValues", atributoRepository.findAll(Sort.by("id"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(br.com.lablims.spring_lablims.domain.Atributo::getId, br.com.lablims.spring_lablims.domain.Atributo::getTipo)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<ColunaDTO> colunas = colunaService.findAll(filter, pageable);
        model.addAttribute("colunas", colunas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(colunas));
        return "pages/coluna/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("coluna") final ColunaDTO colunaDTO) {
        return "pages/coluna/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("coluna") @Valid final ColunaDTO colunaDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("bindingResult.hasErrors"));
            return "pages/coluna/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                colunaService.create(colunaDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("coluna.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/coluna/add";
            }
        }
        return "redirect:/colunas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("coluna", colunaService.get(id));
        return "pages/coluna/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("coluna") @Valid final ColunaDTO colunaDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("bindingResult.hasErrors"));
            return "pages/coluna/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaService.update(id, colunaDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("coluna.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/coluna/edit";
            }
        }
        return "redirect:/colunas";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = colunaService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("coluna.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/colunas";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Coluna>> revisoes = genericRevisionRepository.listaRevisoes(Coluna.class);
        model.addAttribute("audits", revisoes);
        return "pages/coluna/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Coluna coluna = colunaService.findById(id);
        List<EntityRevision<Coluna>> revisoes = genericRevisionRepository.listaRevisoesById(coluna.getId(), Coluna.class);
        model.addAttribute("audits", revisoes);
        return "pages/coluna/audit";
    }

}
