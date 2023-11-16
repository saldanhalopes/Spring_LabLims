package br.com.lablims.spring_lablims.controller.estoque;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import br.com.lablims.spring_lablims.model.EstoqueDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.EstoqueService;
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
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final MaterialRepository materialRepository;
    private final SetorRepository setorRepository;

    private final UnidadeMedidaRepository unidadeMedidaRepository;
    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
        model.addAttribute("materialValues", materialRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Material::getId, Material::getMaterial)));
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
        model.addAttribute("movimentacaoTipoValues", MovimentacaoTipo.values());
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<EstoqueDTO> estoques = estoqueService.findAllOfEstoque(pageable);
        model.addAttribute("estoques", estoques);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(estoques));
        return "estoque/estoque/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("estoque") final EstoqueDTO estoqueDTO) {
        return "estoque/estoque/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("estoque") @Valid final EstoqueDTO estoqueDTO, final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "estoque/estoque/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                estoqueService.create(estoqueDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estoque.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "estoque/estoque/add";
            }
        }
        return "redirect:/estoques";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        model.addAttribute("estoque", estoqueService.get(id));
        return "estoque/estoque/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("estoque", estoqueService.get(id));
        return "estoque/estoque/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("estoque") @Valid final EstoqueDTO estoqueDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "estoque/estoque/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                estoqueService.update(id, estoqueDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estoque.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "estoque/estoque/edit";
            }
        }
        return "redirect:/estoques";
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
            estoqueService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("estoque.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/estoques";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Estoque>> revisoes = genericRevisionRepository.listaRevisoes(Estoque.class);
        model.addAttribute("audits", revisoes);
        return "estoque/estoque/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Estoque estoque = estoqueService.findById(id);
        List<EntityRevision<Estoque>> revisoes = genericRevisionRepository.listaRevisoesById(estoque.getId(), Estoque.class);
        model.addAttribute("audits", revisoes);
        return "estoque/estoque/audit";
    }

}
