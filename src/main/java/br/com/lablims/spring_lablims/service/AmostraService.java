package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AmostraDTO;
import br.com.lablims.spring_lablims.model.LoteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AmostraService {

    private final AmostraRepository amostraRepository;
    private final AmostraTipoRepository amostraTipoRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final CampanhaRepository campanhaRepository;

    public Amostra findById(Integer id){
        return amostraRepository.findById(id).orElse(null);
    }

    public AmostraService(final AmostraRepository amostraRepository,
                          final AmostraTipoRepository amostraTipoRepository,
                          final LoteRepository loteRepository,
                          final UsuarioRepository usuarioRepository,
                          final UnidadeMedidaRepository unidadeMedidaRepository,
                          final CampanhaRepository campanhaRepository) {
        this.amostraRepository = amostraRepository;
        this.amostraTipoRepository = amostraTipoRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.campanhaRepository = campanhaRepository;
    }

    public SimplePage<AmostraDTO> findAll(final String filter, final Pageable pageable) {
        Page<Amostra> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = amostraRepository.findAllById(integerFilter, pageable);
        } else {
            page = amostraRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(amostra -> mapToDTOByAmostras(amostra, new AmostraDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SimplePage<AmostraDTO> findAllOfAmostras(final Pageable pageable) {
        Page<Amostra> page;
        page = amostraRepository.findAllOfAmostras(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(amostra -> mapToDTOByAmostras(amostra, new AmostraDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AmostraDTO get(final Integer id) {
        return amostraRepository.findById(id)
                .map(amostra -> mapToDTO(amostra, new AmostraDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AmostraDTO amostraDTO) {
        final Amostra amostra = new Amostra();
        mapToEntity(amostraDTO, amostra);
        return amostraRepository.save(amostra).getId();
    }

    public void update(final Integer id, final AmostraDTO amostraDTO) {
        final Amostra amostra = amostraRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(amostraDTO, amostra);
        amostraRepository.save(amostra);
    }

    public void delete(final Integer id) {
                amostraRepository.deleteById(id);
        final Amostra amostra = amostraRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        campanhaRepository.findAllByAmostras(amostra)
                .forEach(campanha -> campanha.getAmostras().remove(amostra));
        amostraRepository.delete(amostra);
    }

    private AmostraDTO mapToDTO(final Amostra amostra, final AmostraDTO amostraDTO) {
        amostraDTO.setId(amostra.getId());
        amostraDTO.setCodigoAmostra(amostra.getCodigoAmostra());
        amostraDTO.setUsoInterno(amostra.getUsoInterno());
        amostraDTO.setQtdAmostra(amostra.getQtdAmostra());
        amostraDTO.setFracionamento(amostra.getFracionamento());
        amostraDTO.setDataEntrada(amostra.getDataEntrada());
        amostraDTO.setLocalArmazenamento(amostra.getLocalArmazenamento());
        amostraDTO.setCondicoesArmazenamento(amostra.getCondicoesArmazenamento());
        amostraDTO.setPontoAmostragem(amostra.getPontoAmostragem());
        amostraDTO.setComplemento(amostra.getComplemento());
        amostraDTO.setReferenciaCliente(amostra.getReferenciaCliente());
        amostraDTO.setDataLiberacaoCQ(amostra.getDataLiberacaoCQ());
        amostraDTO.setDataEnvioDocumentacao(amostra.getDataEnvioDocumentacao());
        amostraDTO.setDataImpressao(amostra.getDataImpressao());
        amostraDTO.setObs(amostra.getObs());
        amostraDTO.setUnidade(amostra.getUnidade() == null ? null : amostra.getUnidade().getId());
        amostraDTO.setAmostraTipo(amostra.getAmostraTipo() == null ? null : amostra.getAmostraTipo().getId());
        amostraDTO.setLote(amostra.getLote() == null ? null : amostra.getLote().getId());
        amostraDTO.setUsuarioResponsavel(amostra.getUsuarioResponsavel() == null ? null : amostra.getUsuarioResponsavel().getId());
        amostraDTO.setVersion(amostra.getVersion());
        return amostraDTO;
    }

    private AmostraDTO mapToDTOByAmostras(final Amostra amostra, final AmostraDTO amostraDTO) {
        amostraDTO.setId(amostra.getId());
        amostraDTO.setCodigoAmostra(amostra.getCodigoAmostra());
        amostraDTO.setUsoInterno(amostra.getUsoInterno());
        amostraDTO.setQtdAmostra(amostra.getQtdAmostra());
        amostraDTO.setFracionamento(amostra.getFracionamento());
        amostraDTO.setDataEntrada(amostra.getDataEntrada());
        amostraDTO.setLocalArmazenamento(amostra.getLocalArmazenamento());
        amostraDTO.setCondicoesArmazenamento(amostra.getCondicoesArmazenamento());
        amostraDTO.setPontoAmostragem(amostra.getPontoAmostragem());
        amostraDTO.setComplemento(amostra.getComplemento());
        amostraDTO.setReferenciaCliente(amostra.getReferenciaCliente());
        amostraDTO.setDataLiberacaoCQ(amostra.getDataLiberacaoCQ());
        amostraDTO.setDataEnvioDocumentacao(amostra.getDataEnvioDocumentacao());
        amostraDTO.setDataImpressao(amostra.getDataImpressao());
        amostraDTO.setObs(amostra.getObs());
        amostraDTO.setUnidadeName(amostra.getUnidade() == null ? null : amostra.getUnidade().getUnidade());
        amostraDTO.setAmostraTipoNome(amostra.getAmostraTipo() == null ? null : amostra.getAmostraTipo().getTipo());
        amostraDTO.setLoteNumero(amostra.getLote() == null ? null : amostra.getLote().getLote());
        amostraDTO.setMaterialName(amostra.getLote() == null ? null : amostra.getLote().getMaterial().getMaterial());
        amostraDTO.setMaterialCodigo(amostra.getLote() == null ? null : amostra.getLote().getMaterial().getCodigo());
        amostraDTO.setResponsavel(amostra.getUsuarioResponsavel() == null ? null : amostra.getUsuarioResponsavel().getNome());
        amostraDTO.setVersion(amostra.getVersion());
        return amostraDTO;
    }

    private Amostra mapToEntity(final AmostraDTO amostraDTO, final Amostra amostra) {
        amostra.setCodigoAmostra(amostraDTO.getCodigoAmostra());
        amostra.setUsoInterno(amostraDTO.getUsoInterno());
        amostra.setQtdAmostra(amostraDTO.getQtdAmostra());
        amostra.setFracionamento(amostraDTO.getFracionamento());
        amostra.setDataEntrada(amostraDTO.getDataEntrada());
        amostra.setLocalArmazenamento(amostraDTO.getLocalArmazenamento());
        amostra.setCondicoesArmazenamento(amostraDTO.getCondicoesArmazenamento());
        amostra.setPontoAmostragem(amostraDTO.getPontoAmostragem());
        amostra.setComplemento(amostraDTO.getComplemento());
        amostra.setReferenciaCliente(amostraDTO.getReferenciaCliente());
        amostra.setDataLiberacaoCQ(amostraDTO.getDataLiberacaoCQ());
        amostra.setDataEnvioDocumentacao(amostraDTO.getDataEnvioDocumentacao());
        amostra.setDataImpressao(amostraDTO.getDataImpressao());
        amostra.setObs(amostraDTO.getObs());
        final UnidadeMedida unidadeMedida = amostraDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(amostraDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("Unidade Medida nao encontrada"));
        amostra.setUnidade(unidadeMedida);
        final AmostraTipo amostraTipo = amostraDTO.getAmostraTipo() == null ? null : amostraTipoRepository.findById(amostraDTO.getAmostraTipo())
                .orElseThrow(() -> new NotFoundException("Amostra Tipo nao encontrada"));
        amostra.setAmostraTipo(amostraTipo);
        final Lote lote = amostraDTO.getLote() == null ? null : loteRepository.findById(amostraDTO.getLote())
                .orElseThrow(() -> new NotFoundException("Lote nao encontrado"));
        amostra.setLote(lote);
        final Usuario usuarioResponsavel = amostraDTO.getUsuarioResponsavel() == null ? null : usuarioRepository.findByUsername(amostraDTO.getResponsavel())
                .orElseThrow(() -> new NotFoundException("Usuario Responsavel nao encontrado"));
        amostra.setUsuarioResponsavel(usuarioResponsavel);
        return amostra;
    }

    public boolean codigoAmostraExists(final String amostra) {
        return amostraRepository.existsByCodigoAmostraIgnoreCase(amostra);
    }

}
