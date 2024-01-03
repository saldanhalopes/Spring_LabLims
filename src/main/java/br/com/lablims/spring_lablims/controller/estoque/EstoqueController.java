package br.com.lablims.spring_lablims.controller.estoque;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import br.com.lablims.spring_lablims.model.EstoqueDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.service.ArquivosService;
import br.com.lablims.spring_lablims.service.EstoqueService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final MaterialRepository materialRepository;
    private final SetorRepository setorRepository;

    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final StorageEnderecoRepository storageEnderecoRepository;
    private final ArquivosRepository arquivosRepository;
    private final ArquivosService arquivosService;
    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("setorValues", setorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Setor::getId, Setor::getSetor)));
        model.addAttribute("materialValues", materialRepository.findListOfMateriais(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Material::getId,
                        material -> material.getCodigo() + " - " + material.getMaterial()
                                + " (" + material.getCategoria().getCategoria() + " / " + material.getFornecedor().getFornecedor()
                                + ") - Unid: " + material.getUnidade().getUnidade())));
        model.addAttribute("unidadeValues", unidadeMedidaRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UnidadeMedida::getId, UnidadeMedida::getUnidade)));
        model.addAttribute("movimentacaoTipoValues", MovimentacaoTipo.values());
        model.addAttribute("storageEnderecoValues", storageEnderecoRepository.findListOfStorageEnderecos(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(StorageEndereco::getId,
                        storageEndereco -> storageEndereco.getStorage().getSetor().getSiglaSetor()
                                + " - " + storageEndereco.getStorage().getStorage() + " - " + storageEndereco.getEndereco()
                                + " - Cond. Arm.: " + storageEndereco.getStorage().getTipo().getCondicoesArmazenamento())));
    }

    @Autowired
    private UsuarioService usuarioService;

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
        final SimplePage<EstoqueDTO> estoques = estoqueService.findAll(filter, pag);
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
            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("bindingResult.hasErrors"));
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

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @GetMapping("/upload/{id}")
    public String uploads(@PathVariable final Integer id,
                          @ModelAttribute("arquivos") final Arquivos arquivos,
                          final Model model) {
        final List<Arquivos> arquivoss = estoqueService.findArquivosByEstoque(id);
        model.addAttribute("estoque", estoqueService.get(id));
        model.addAttribute("arquivoss", arquivoss);
        return "estoque/estoque/upload";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
    @PostMapping("/upload/{id}")
    public String upload(@PathVariable final Integer id,
                         @RequestParam("arquivo") MultipartFile file, final Model model,
                         final RedirectAttributes redirectAttributes, @ModelAttribute("motivo") String motivo,
                         Principal principal, @ModelAttribute("password") String pass) throws IOException {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            Estoque estoque  = estoqueService.findEstoqueWithArquivos(id);
            Arquivos arquivos = new Arquivos();
            arquivos.setArquivo(file.getBytes());
            arquivos.setTamanho(file.getSize());
            arquivos.setNome(file.getOriginalFilename());
            arquivos.setTipo(file.getContentType());
            arquivos.setDescricao(motivo);
            arquivosRepository.save(arquivos);
            estoque.getArquivos().add(arquivos);
            estoqueService.update(estoque);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("arquivos.update.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
            return "redirect:/estoques/upload/{id}";
        }
        return "redirect:/estoques/upload/{id}";
    }

    @PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "')")
    @PostMapping("/{eqId}/arquivo/delete/{id}")
    public String deleteArquivo(@PathVariable final Integer eqId,
                                @PathVariable final Integer id,
                                final RedirectAttributes redirectAttributes,
                                @ModelAttribute("motivo") String motivo,
                                Principal principal,
                                @ModelAttribute("password") String pass) {
        if (usuarioService.validarUser(principal.getName(), pass)) {
            CustomRevisionEntity.setMotivoText(motivo);
            arquivosService.deleteEstoque(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("arquivos.delete.success"));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.error"));
        }
        return "redirect:/estoques/upload/{eqId}";
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
