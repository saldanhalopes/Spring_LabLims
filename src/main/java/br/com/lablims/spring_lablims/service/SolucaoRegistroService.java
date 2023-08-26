package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.SolucaoEquipamento;
import br.com.lablims.spring_lablims.domain.SolucaoParemetro;
import br.com.lablims.spring_lablims.domain.SolucaoReagente;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.domain.SolucaoTipo;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoRegistroDTO;
import br.com.lablims.spring_lablims.repos.SolucaoEquipamentoRepository;
import br.com.lablims.spring_lablims.repos.SolucaoParemetroRepository;
import br.com.lablims.spring_lablims.repos.SolucaoReagenteRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.repos.SolucaoTipoRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SolucaoRegistroService {

    private final SolucaoRegistroRepository solucaoRegistroRepository;
    private final SolucaoTipoRepository solucaoTipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final SolucaoParemetroRepository solucaoParemetroRepository;
    private final SolucaoEquipamentoRepository solucaoEquipamentoRepository;
    private final SolucaoReagenteRepository solucaoReagenteRepository;

    public SolucaoRegistro findById(Integer id){
        return solucaoRegistroRepository.findById(id).orElse(null);
    }

    public SolucaoRegistroService(final SolucaoRegistroRepository solucaoRegistroRepository,
            final SolucaoTipoRepository solucaoTipoRepository,
            final UsuarioRepository usuarioRepository,
            final UnidadeMedidaRepository unidadeMedidaRepository,
            final SolucaoParemetroRepository solucaoParemetroRepository,
            final SolucaoEquipamentoRepository solucaoEquipamentoRepository,
            final SolucaoReagenteRepository solucaoReagenteRepository) {
        this.solucaoRegistroRepository = solucaoRegistroRepository;
        this.solucaoTipoRepository = solucaoTipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.solucaoParemetroRepository = solucaoParemetroRepository;
        this.solucaoEquipamentoRepository = solucaoEquipamentoRepository;
        this.solucaoReagenteRepository = solucaoReagenteRepository;
    }

    public SimplePage<SolucaoRegistroDTO> findAll(final String filter, final Pageable pageable) {
        Page<SolucaoRegistro> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = solucaoRegistroRepository.findAllById(integerFilter, pageable);
        } else {
            page = solucaoRegistroRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(solucaoRegistro -> mapToDTO(solucaoRegistro, new SolucaoRegistroDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SolucaoRegistroDTO get(final Integer id) {
        return solucaoRegistroRepository.findById(id)
                .map(solucaoRegistro -> mapToDTO(solucaoRegistro, new SolucaoRegistroDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SolucaoRegistroDTO solucaoRegistroDTO) {
        final SolucaoRegistro solucaoRegistro = new SolucaoRegistro();
        mapToEntity(solucaoRegistroDTO, solucaoRegistro);
        return solucaoRegistroRepository.save(solucaoRegistro).getId();
    }

    public void update(final Integer id, final SolucaoRegistroDTO solucaoRegistroDTO) {
        final SolucaoRegistro solucaoRegistro = solucaoRegistroRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(solucaoRegistroDTO, solucaoRegistro);
        solucaoRegistroRepository.save(solucaoRegistro);
    }

    public void delete(final Integer id) {
        solucaoRegistroRepository.deleteById(id);
    }

    private SolucaoRegistroDTO mapToDTO(final SolucaoRegistro solucaoRegistro,
            final SolucaoRegistroDTO solucaoRegistroDTO) {
        solucaoRegistroDTO.setId(solucaoRegistro.getId());
        solucaoRegistroDTO.setDescricao(solucaoRegistro.getDescricao());
        solucaoRegistroDTO.setLote(solucaoRegistro.getLote());
        solucaoRegistroDTO.setTipo(solucaoRegistro.getTipo());
        solucaoRegistroDTO.setReferencia(solucaoRegistro.getReferencia());
        solucaoRegistroDTO.setArmazenamento(solucaoRegistro.getArmazenamento());
        solucaoRegistroDTO.setDataPreparo(solucaoRegistro.getDataPreparo());
        solucaoRegistroDTO.setDataValidade(solucaoRegistro.getDataValidade());
        solucaoRegistroDTO.setDataConferencia(solucaoRegistro.getDataConferencia());
        solucaoRegistroDTO.setQtd(solucaoRegistro.getQtd());
        solucaoRegistroDTO.setAtivo(solucaoRegistro.getAtivo());
        solucaoRegistroDTO.setObs(solucaoRegistro.getObs());
        solucaoRegistroDTO.setSolucaoTipo(solucaoRegistro.getSolucaoTipo() == null ? null : solucaoRegistro.getSolucaoTipo().getId());
        solucaoRegistroDTO.setCriador(solucaoRegistro.getCriador() == null ? null : solucaoRegistro.getCriador().getId());
        solucaoRegistroDTO.setConferente(solucaoRegistro.getConferente() == null ? null : solucaoRegistro.getConferente().getId());
        solucaoRegistroDTO.setUnidade(solucaoRegistro.getUnidade() == null ? null : solucaoRegistro.getUnidade().getId());
        solucaoRegistroDTO.setVersion(solucaoRegistro.getVersion());
        return solucaoRegistroDTO;
    }

    private SolucaoRegistro mapToEntity(final SolucaoRegistroDTO solucaoRegistroDTO,
            final SolucaoRegistro solucaoRegistro) {
        solucaoRegistro.setDescricao(solucaoRegistroDTO.getDescricao());
        solucaoRegistro.setLote(solucaoRegistroDTO.getLote());
        solucaoRegistro.setTipo(solucaoRegistroDTO.getTipo());
        solucaoRegistro.setReferencia(solucaoRegistroDTO.getReferencia());
        solucaoRegistro.setArmazenamento(solucaoRegistroDTO.getArmazenamento());
        solucaoRegistro.setDataPreparo(solucaoRegistroDTO.getDataPreparo());
        solucaoRegistro.setDataValidade(solucaoRegistroDTO.getDataValidade());
        solucaoRegistro.setDataConferencia(solucaoRegistroDTO.getDataConferencia());
        solucaoRegistro.setQtd(solucaoRegistroDTO.getQtd());
        solucaoRegistro.setAtivo(solucaoRegistroDTO.getAtivo());
        solucaoRegistro.setObs(solucaoRegistroDTO.getObs());
        final SolucaoTipo solucaoTipo = solucaoRegistroDTO.getSolucaoTipo() == null ? null : solucaoTipoRepository.findById(solucaoRegistroDTO.getSolucaoTipo())
                .orElseThrow(() -> new NotFoundException("solucaoTipo not found"));
        solucaoRegistro.setSolucaoTipo(solucaoTipo);
        final Usuario criador = solucaoRegistroDTO.getCriador() == null ? null : usuarioRepository.findById(solucaoRegistroDTO.getCriador())
                .orElseThrow(() -> new NotFoundException("criador not found"));
        solucaoRegistro.setCriador(criador);
        final Usuario conferente = solucaoRegistroDTO.getConferente() == null ? null : usuarioRepository.findById(solucaoRegistroDTO.getConferente())
                .orElseThrow(() -> new NotFoundException("conferente not found"));
        solucaoRegistro.setConferente(conferente);
        final UnidadeMedida unidade = solucaoRegistroDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(solucaoRegistroDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("unidade not found"));
        solucaoRegistro.setUnidade(unidade);
        return solucaoRegistro;
    }

    public String getReferencedWarning(final Integer id) {
        final SolucaoRegistro solucaoRegistro = solucaoRegistroRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final SolucaoParemetro solucaoRegistroSolucaoParemetro = solucaoParemetroRepository.findFirstBySolucaoRegistro(solucaoRegistro);
        if (solucaoRegistroSolucaoParemetro != null) {
            return WebUtils.getMessage("solucaoRegistro.solucaoParemetro.solucaoRegistro.referenced", solucaoRegistroSolucaoParemetro.getId());
        }
        final SolucaoEquipamento solucaoRegistroSolucaoEquipamento = solucaoEquipamentoRepository.findFirstBySolucaoRegistro(solucaoRegistro);
        if (solucaoRegistroSolucaoEquipamento != null) {
            return WebUtils.getMessage("solucaoRegistro.solucaoEquipamento.solucaoRegistro.referenced", solucaoRegistroSolucaoEquipamento.getId());
        }
        final SolucaoReagente solucaoRegistroSolucaoReagente = solucaoReagenteRepository.findFirstBySolucaoRegistro(solucaoRegistro);
        if (solucaoRegistroSolucaoReagente != null) {
            return WebUtils.getMessage("solucaoRegistro.solucaoReagente.solucaoRegistro.referenced", solucaoRegistroSolucaoReagente.getId());
        }
        return null;
    }

}
