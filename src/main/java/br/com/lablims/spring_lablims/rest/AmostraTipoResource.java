package br.com.lablims.spring_lablims.rest;

import br.com.lablims.spring_lablims.config.EntityRevision;
import br.com.lablims.spring_lablims.config.GenericRevisionRepository;
import br.com.lablims.spring_lablims.domain.AmostraTipo;
import br.com.lablims.spring_lablims.model.AmostraTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.service.AmostraTipoService;
import br.com.lablims.spring_lablims.util.UserRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/amostraTipoes", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.MASTERUSER + "', '" + UserRoles.POWERUSER + "')")
public class AmostraTipoResource {

    private final AmostraTipoService amostraTipoService;

    @Autowired
    private GenericRevisionRepository genericRevisionRepository;

    @Operation(
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<SimplePage<AmostraTipoDTO>> getAllAmostraTipoes(
            @RequestParam(required = false, name = "filter") final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(amostraTipoService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmostraTipoDTO> getAmostraTipo(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(amostraTipoService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createAmostraTipo(
            @RequestBody @Valid final AmostraTipoDTO amostraTipoDTO) {
        final Integer createdId = amostraTipoService.create(amostraTipoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateAmostraTipo(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final AmostraTipoDTO amostraTipoDTO) {
        amostraTipoService.update(id, amostraTipoDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAmostraTipo(@PathVariable(name = "id") final Integer id) {
        amostraTipoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit")
    public ResponseEntity<List<EntityRevision<AmostraTipo>>> getRevisions() {
        return ResponseEntity.ok(genericRevisionRepository.listaRevisoes(AmostraTipo.class));
    }

    @GetMapping("/audit/{id}")
    public ResponseEntity<List<EntityRevision<AmostraTipo>>> getAllRevisions(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(genericRevisionRepository.listaRevisoesById(amostraTipoService.findById(id).getId(), AmostraTipo.class));
    }

}
