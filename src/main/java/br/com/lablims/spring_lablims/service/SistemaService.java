package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Sistema;
import br.com.lablims.spring_lablims.model.SistemaDTO;
import br.com.lablims.spring_lablims.repos.SistemaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SistemaService {

    private final SistemaRepository sistemaRepository;

    public Sistema findById(Integer id){
        return sistemaRepository.findById(id).orElse(null);
    }

    public SistemaDTO get(final Integer id) {
        return sistemaRepository.findById(id)
                .map(turno -> mapToDTO(turno, new SistemaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SistemaDTO turnoDTO) {
        final Sistema turno = new Sistema();
        mapToEntity(turnoDTO, turno);
        return sistemaRepository.save(turno).getId();
    }

    public void update(final Integer id, final SistemaDTO turnoDTO) {
        final Sistema turno = sistemaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(turnoDTO, turno);
        sistemaRepository.save(turno);
    }

    public void delete(final Integer id) {
        sistemaRepository.deleteById(id);
    }

    private SistemaDTO mapToDTO(final Sistema sistema, final SistemaDTO sistemaDTO) {
        sistemaDTO.setId(sistema.getId());
        sistemaDTO.setSistemaNome(sistema.getSistemaNome());
        sistemaDTO.setSistemaCriador(sistema.getSistemaCriador());
        sistemaDTO.setDetalhes(sistema.getDetalhes());
        sistemaDTO.setBuilderVersao(sistema.getBuilderVersao());
        sistemaDTO.setVersion(sistema.getVersion());
        return sistemaDTO;
    }

    private Sistema mapToEntity(final SistemaDTO sistemaDTO, final Sistema sistema) {
        sistema.setSistemaNome(sistemaDTO.getSistemaNome());
        sistema.setSistemaCriador(sistemaDTO.getSistemaCriador());
        sistema.setDetalhes(sistemaDTO.getDetalhes());
        sistema.setBuilderVersao(sistemaDTO.getBuilderVersao());
        return sistema;
    }

}
