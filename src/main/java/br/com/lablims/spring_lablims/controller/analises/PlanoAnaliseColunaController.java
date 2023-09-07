package br.com.lablims.spring_lablims.controller.analises;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.PlanoAnaliseColunaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaRepository;
import br.com.lablims.spring_lablims.repos.GenericRevisionRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.service.PlanoAnaliseColunaService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.CustomCollectors;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/planoAnaliseColunas")
public class PlanoAnaliseColunaController {

    private final PlanoAnaliseColunaService planoAnaliseColunaService;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final ColunaRepository colunaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    public PlanoAnaliseColunaController(final PlanoAnaliseColunaService planoAnaliseColunaService,
                                        final PlanoAnaliseRepository planoAnaliseRepository,
                                        final ColunaRepository colunaRepository,
                                        final UnidadeMedidaRepository unidadeMedidaRepository) {
        this.planoAnaliseColunaService = planoAnaliseColunaService;
        this.planoAnaliseRepository = planoAnaliseRepository;
        this.colunaRepository = colunaRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("planoAnaliseValues", planoAnaliseRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(PlanoAnalise::getId, PlanoAnalise::getDescricao)));
        model.addAttribute("colunaValues", colunaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Coluna::getId, Coluna::getCodigo)));
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
                       @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
                       final Model model) {
        final SimplePage<PlanoAnaliseColunaDTO> planoAnaliseColunas = planoAnaliseColunaService.findAll(filter, pageable);
        model.addAttribute("planoAnaliseColunas", planoAnaliseColunas);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(planoAnaliseColunas));
        return "pages/planoAnaliseColuna/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("planoAnaliseColuna") final PlanoAnaliseColunaDTO planoAnaliseColunaDTO) {
        return "pages/planoAnaliseColuna/add";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/add")
    public String add(@ModelAttribute("planoAnaliseColuna") @Valid final PlanoAnaliseColunaDTO planoAnaliseColunaDTO, final Model model,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/planoAnaliseColuna/add";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText("Novo Registro");
                planoAnaliseColunaService.create(planoAnaliseColunaDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnaliseColuna.create.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "pages/planoAnaliseColuna/add";
            }
        }
        return "redirect:/planoAnaliseColunas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id, final Model model) {
        model.addAttribute("planoAnaliseColuna", planoAnaliseColunaService.get(id));
        return "pages/planoAnaliseColuna/edit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "')")
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Integer id,
                       @ModelAttribute("planoAnaliseColuna") @Valid final PlanoAnaliseColunaDTO planoAnaliseColunaDTO,
                       final BindingResult bindingResult, final Model model,
                       final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                       Principal principal, @ModelAttribute("password") String pass) {
        if (bindingResult.hasErrors()) {
            return "pages/planoAnaliseColuna/edit";
        } else {
            if (usuarioService.validarUser(principal.getName(), pass)) {
                CustomRevisionEntity.setMotivoText(motivo);
                planoAnaliseColunaService.update(id, planoAnaliseColunaDTO);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("planoAnaliseColuna.update.success"));
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
                return "pages/planoAnaliseColuna/edit";
            }
        }
        return "redirect:/planoAnaliseColunas";
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
            planoAnaliseColunaService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("planoAnaliseColuna.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
        }
        return "redirect:/planoAnaliseColunas";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<PlanoAnaliseColuna>> revisoes = genericRevisionRepository.listaRevisoes(PlanoAnaliseColuna.class);
        model.addAttribute("audits", revisoes);
        return "pages/planoAnaliseColuna/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        PlanoAnaliseColuna planoAnaliseColuna = planoAnaliseColunaService.findById(id);
        List<EntityRevision<PlanoAnaliseColuna>> revisoes = genericRevisionRepository.listaRevisoesById(planoAnaliseColuna.getId(), PlanoAnaliseColuna.class);
        model.addAttribute("audits", revisoes);
        return "pages/planoAnaliseColuna/audit";
    }


}
