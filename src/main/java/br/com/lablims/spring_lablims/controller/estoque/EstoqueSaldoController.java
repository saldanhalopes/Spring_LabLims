package br.com.lablims.spring_lablims.controller.estoque;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.Estoque;
import br.com.lablims.spring_lablims.domain.EstoqueSaldo;
import br.com.lablims.spring_lablims.model.AnaliseTipoDTO;
import br.com.lablims.spring_lablims.model.EstoqueDTO;
import br.com.lablims.spring_lablims.model.EstoqueSaldoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.service.EstoqueSaldoService;
import br.com.lablims.spring_lablims.service.EstoqueService;
import br.com.lablims.spring_lablims.service.UsuarioService;
import br.com.lablims.spring_lablims.util.UserRoles;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/estoqueSaldos")
public class EstoqueSaldoController {

    private final EstoqueSaldoService estoqueSaldoService;
    private final EstoqueService estoqueService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;


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
        final SimplePage<EstoqueSaldoDTO> estoqueSaldos = estoqueSaldoService.findAll(filter, pag);
        model.addAttribute("estoqueSaldos", estoqueSaldos);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(estoqueSaldos));
        return "estoque/estoqueSaldo/list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable final Integer id,
                          @SortDefault(sort = "id", direction = Sort.Direction.DESC) @PageableDefault(size = 50) final Pageable pageable,
                          final Model model) {
        final SimplePage<EstoqueDTO> estoques = estoqueService.findAllByMaterial(id, pageable);
        model.addAttribute("estoques", estoques);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(estoques));
        return "estoque/estoqueSaldo/details";
    }


    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit")
    public String getRevisions(Model model) {
        List<EntityRevision<Estoque>> revisoes = genericRevisionRepository.listaRevisoes(Estoque.class);
        model.addAttribute("audits", revisoes);
        return "estoque/estoqueSaldo/audit";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @RequestMapping("/audit/{id}")
    public String getRevisions(Model model, @PathVariable final Integer id) {
        EstoqueSaldo estoqueSaldo = estoqueSaldoService.findById(id);
        List<EntityRevision<EstoqueSaldo>> revisoes = genericRevisionRepository.listaRevisoesById(estoqueSaldo.getId(), Estoque.class);
        model.addAttribute("audits", revisoes);
        return "estoque/estoqueSaldo/audit";
    }

}
