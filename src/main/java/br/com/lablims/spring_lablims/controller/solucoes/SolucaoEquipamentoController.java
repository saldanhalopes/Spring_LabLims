package br.com.lablims.spring_lablims.controller.solucoes;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.SolucaoEquipamento;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoEquipamentoDTO;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.service.SolucaoEquipamentoService;
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
@RequestMapping("/solucaoEquipamentos")
public class SolucaoEquipamentoController {

    private final SolucaoEquipamentoService solucaoEquipamentoService;
    private final SolucaoRegistroRepository solucaoRegistroRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("solucaoRegistroValues", solucaoRegistroRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(SolucaoRegistro::getId, SolucaoRegistro::getDescricao)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<SolucaoEquipamentoDTO> solucaoEquipamentos = solucaoEquipamentoService.findAll(filter, pageable);
        model.addAttribute("solucaoEquipamentos", solucaoEquipamentos);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(solucaoEquipamentos));
        return "pages/solucaoEquipamento/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("solucaoEquipamento") final SolucaoEquipamentoDTO solucaoEquipamentoDTO) {
        return "pages/solucaoEquipamento/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("solucaoEquipamento") @Valid final SolucaoEquipamentoDTO solucaoEquipamentoDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/solucaoEquipamento/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                solucaoEquipamentoService.create(solucaoEquipamentoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoEquipamento.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/solucaoEquipamento/add";
            }
        }
        return "redirect:/solucaoEquipamentos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("solucaoEquipamento", solucaoEquipamentoService.get(id));
        return "pages/solucaoEquipamento/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("solucaoEquipamento") @Valid final SolucaoEquipamentoDTO solucaoEquipamentoDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/solucaoEquipamento/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                solucaoEquipamentoService.update(id, solucaoEquipamentoDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("solucaoEquipamento.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "pages/solucaoEquipamento/edit";
            }
        }
        return "redirect:/solucaoEquipamentos";
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
            solucaoEquipamentoService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("solucaoEquipamento.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/solucaoEquipamentos";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<SolucaoEquipamento>> revisoes = genericRevisionRepository.listaRevisoes(SolucaoEquipamento.class);
        model.addAttribute("audits", revisoes);
        return "pages/solucaoEquipamento/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        SolucaoEquipamento solucaoEquipamento = solucaoEquipamentoService.findById(id);
        List<EntityRevision<SolucaoEquipamento>> revisoes = genericRevisionRepository.listaRevisoesById(solucaoEquipamento.getId(), SolucaoEquipamento.class);
        model.addAttribute("audits", revisoes);
        return "pages/solucaoEquipamento/audit";
    }

}
