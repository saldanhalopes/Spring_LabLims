package br.com.lablims.spring_lablims.controller.parametros;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.model.AnaliseDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseTipoRepository;
import br.com.lablims.spring_lablims.service.AnaliseService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/analises")
public class AnaliseController {

    private final AnaliseService analiseService;

    private final AnaliseTipoRepository analiseTipoRepository;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("analiseTipoValues", analiseTipoRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AnaliseTipo::getId, AnaliseTipo::getAnaliseTipo)));
    }
    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @RequestParam(defaultValue = "50") final int size,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "id") String sort,
                       @RequestParam(required = false) String sortDir,
                       final Model model) {
        Pageable pag = PageRequest.of(page, size, WebUtils.getSortDirection(sortDir), sort);
        model.addAttribute("filter", filter);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        model.addAttribute("sortDir", sortDir);
        final SimplePage<AnaliseDTO> analises = analiseService.findAll(filter, pag);
        model.addAttribute("analises", analises);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(analises));
        return "parameters/analise/list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id, final Model model) {
        model.addAttribute("analise", analiseService.get(id));
        return "parameters/analise/details";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("analise") final AnaliseDTO analiseDTO) {
        return "parameters/analise/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("analise") @Valid final AnaliseDTO analiseDTO,  final BindingResult bindingResult,
                      final Model model, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("bindingResult.hasErrors"));
            return "parameters/analise/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                analiseService.create(analiseDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("analise.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/analise/add";
            }
        }
        return "redirect:/analises";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("analise", analiseService.get(id));
        return "parameters/analise/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("analise") @Valid final AnaliseDTO analiseDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("bindingResult.hasErrors"));
            return "parameters/analise/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                analiseService.update(id, analiseDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("analise.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
                return "parameters/analise/edit";
            }
        }
        return "redirect:/analises";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Integer id,
                         final RedirectAttributes redirectAttributes,
                         @ModelAttribute("motivo") String motivo,
                         Principal principal,
                         @ModelAttribute("password") String pass) {
        final String referencedWarning = analiseService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                analiseService.delete(id);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("analise.delete.success"));
            } else {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            }
        }
        return "redirect:/analises";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Analise>> revisoes = genericRevisionRepository.listaRevisoes(Analise.class);
        model.addAttribute("audits", revisoes);
        return "parameters/analise/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        Analise analise = analiseService.findById(id);
        List<EntityRevision<Analise>> revisoes = genericRevisionRepository.listaRevisoesById(analise.getId(), Analise.class);
        model.addAttribute("audits", revisoes);
        return "parameters/analise/audit";
    }


}
