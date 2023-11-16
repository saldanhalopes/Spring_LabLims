package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Cliente;
import br.com.lablims.spring_lablims.model.ClienteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ClienteRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    public Cliente findById(Integer id){
        return clienteRepository.findById(id).orElse(null);
    }

    public SimplePage<ClienteDTO> findAll(final String filter, final Pageable pageable) {
        Page<Cliente> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = clienteRepository.findAllById(integerFilter, pageable);
        } else {
            page = clienteRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(Cliente -> mapToDTO(Cliente, new ClienteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ClienteDTO get(final Integer id) {
        return clienteRepository.findById(id)
                .map(cliente -> mapToDTO(cliente, new ClienteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ClienteDTO clienteDTO) {
        final Cliente cliente = new Cliente();
        mapToEntity(clienteDTO, cliente);
        return clienteRepository.save(cliente).getId();
    }

    public void update(final Integer id, final ClienteDTO clienteDTO) {
        final Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(clienteDTO, cliente);
        clienteRepository.save(cliente);
    }

    public void delete(final Integer id) {
        clienteRepository.deleteById(id);
    }

    private ClienteDTO mapToDTO(final Cliente cliente,
            final ClienteDTO clienteDTO) {
        clienteDTO.setId(cliente.getId());
        clienteDTO.setCliente(cliente.getCliente());
        clienteDTO.setDescricao(cliente.getDescricao());
        clienteDTO.setVersion(cliente.getVersion());
        return clienteDTO;
    }

    private Cliente mapToEntity(final ClienteDTO clienteDTO,
            final Cliente cliente) {
        cliente.setCliente(clienteDTO.getCliente());
        cliente.setDescricao(clienteDTO.getDescricao());
        return cliente;
    }

}
