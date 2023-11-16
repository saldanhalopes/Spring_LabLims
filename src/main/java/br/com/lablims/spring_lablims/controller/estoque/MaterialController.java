package br.com.lablims.spring_lablims.controller.estoque;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.MaterialDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.FornecedorRepository;
import br.com.lablims.spring_lablims.repos.MaterialTipoRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.MaterialService;
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
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialService materialService;
    private final MaterialTipoRepository materialTipoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final FornecedorRepository fornecedorRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("materialTipoValues", materialTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MaterialTipo::getId, MaterialTipo::getTipo)));
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
        model.addAttribute("fornecedorValues", fornecedorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Fornecedor::getId, Fornecedor::getFornecedor)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<MaterialDTO> materials = materialService.findAllOfMateriais(pageable);
        model.addAttribute("materials", materials);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(materials));
        return "estoque/material/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("material") final MaterialDTO materialDTO) {
        return "estoque/material/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("material") @Valid final MaterialDTO materialDTO, final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "estoque/material/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                materialService.create(materialDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("material.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "estoque/material/add";
            }
        }
        return "redirect:/materials";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        model.addAttribute("material", materialService.get(id));
        return "estoque/material/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("material", materialService.get(id));
        return "estoque/material/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("material") @Valid final MaterialDTO materialDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "estoque/material/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                materialService.update(id, materialDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("material.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "estoque/material/edit";
            }
        }
        return "redirect:/materials";
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
            materialService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("material.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/materials";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Material>> revisoes = genericRevisionRepository.listaRevisoes(Material.class);
        model.addAttribute("audits", revisoes);
        return "estoque/material/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Material material = materialService.findById(id);
        List<EntityRevision<Material>> revisoes = genericRevisionRepository.listaRevisoesById(material.getId(), Material.class);
        model.addAttribute("audits", revisoes);
        return "estoque/material/audit";
    }

}
