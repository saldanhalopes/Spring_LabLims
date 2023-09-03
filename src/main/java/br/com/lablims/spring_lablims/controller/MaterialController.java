package br.com.lablims.spring_lablims.controller;

import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.MaterialTipo;
import br.com.lablims.spring_lablims.model.MaterialDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.MaterialTipoRepository;
import br.com.lablims.spring_lablims.service.MaterialService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.config.EntityRevision;

import java.util.List;


@Controller
@RequestMapping("/materials")
@PreAuthorize("hasAuthority('" + UserRoles.ADMIN + "')")
public class MaterialController {

    private final MaterialService materialService;
    private final MaterialTipoRepository materialTipoRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;


    public MaterialController(final MaterialService materialService,
            final MaterialTipoRepository materialTipoRepository) {
        this.materialService = materialService;
        this.materialTipoRepository = materialTipoRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("tipoMaterialValues", materialTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MaterialTipo::getId, MaterialTipo::getSigla)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<MaterialDTO> materials = materialService.findAll(filter, pageable);
        model.addAttribute("materials", materials);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(materials));
        return "material/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("material") final MaterialDTO materialDTO) {
        return "material/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("material") @Valid final MaterialDTO materialDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "material/add";
        }
        materialService.create(materialDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("material.create.success"));
        return "redirect:/materials";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("material", materialService.get(id));
        return "material/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
            @ModelAttribute("material") @Valid final MaterialDTO materialDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "material/edit";
        }
        materialService.update(id, materialDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("material.update.success"));
        return "redirect:/materials";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = materialService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            materialService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("material.delete.success"));
        }
        return "redirect:/materials";
    }

    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Material>> revisoes = genericRevisionRepository.listaRevisoes(Material.class);
        model.addAttribute("audits", revisoes);
        return "/material/audit";
    }

    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Material material = materialService.findById(id);
        List<EntityRevision<Material>> revisoes = genericRevisionRepository.listaRevisoesById(material.getId(), Material.class);
        model.addAttribute("audits", revisoes);
        return "/material/audit";
    }

}
