package br.com.lablims.spring_lablims.controller.colunas;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.StorageTipo;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.model.ColunaStorageTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.service.ColunaStorageTipoService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/colunaStorageTipos")
public class ColunaStorageTipoController {

    private final ColunaStorageTipoService colunaStorageTipoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<ColunaStorageTipoDTO> colunaStorageTipos = colunaStorageTipoService.findAll(filter, pageable);
        model.addAttribute("colunaStorageTipos", colunaStorageTipos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(colunaStorageTipos));
        return "pages/colunaStorageTipo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("colunaStorageTipo") final ColunaStorageTipoDTO colunaStorageTipoDTO) {
        return "pages/colunaStorageTipo/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("colunaStorageTipo") @Valid final ColunaStorageTipoDTO colunaStorageTipoDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/colunaStorageTipo/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                colunaStorageTipoService.create(colunaStorageTipoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("colunaStorageTipo.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/colunaStorageTipo/add";
            }
        }
        return "redirect:/colunaStorageTipos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("colunaStorageTipo", colunaStorageTipoService.get(id));
        return "pages/colunaStorageTipo/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("colunaStorageTipo") @Valid final ColunaStorageTipoDTO colunaStorageTipoDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/colunaStorageTipo/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaStorageTipoService.update(id, colunaStorageTipoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("colunaStorageTipo.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/colunaStorageTipo/edit";
            }
        }
        return "redirect:/colunaStorageTipos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = colunaStorageTipoService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                colunaStorageTipoService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("colunaStorageTipo.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/colunaStorageTipos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<StorageTipo>> revisoes = genericRevisionRepository.listaRevisoes(StorageTipo.class);
        model.addAttribute("audits", revisoes);
        return "pages/colunaStorageTipo/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        StorageTipo storageTipo = colunaStorageTipoService.findById(id);
        List<EntityRevision<StorageTipo>> revisoes = genericRevisionRepository.listaRevisoesById(storageTipo.getId(), StorageTipo.class);
        model.addAttribute("audits", revisoes);
        return "pages/storageTipo/audit";
    }
}
