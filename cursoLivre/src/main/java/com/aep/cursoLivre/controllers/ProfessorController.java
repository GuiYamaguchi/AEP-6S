
package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.dtos.ProfessorDTO;
import com.aep.cursoLivre.models.ProfessorModel;
import com.aep.cursoLivre.services.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @Operation(description = "Lista todos os professores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Professores listados com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<ProfessorDTO>> listarProfessores() {

        List<ProfessorDTO> professores =
                professorService.listarProfessores()
                        .stream()
                        .map(ProfessorDTO::fromModel)
                        .toList();

        return ResponseEntity.ok(professores);
    }

    @Operation(description = "Busca professor pelo professorId")
    @GetMapping("/{professorId}")
    public ResponseEntity<ProfessorDTO> buscarPorProfessorId(
            @PathVariable Long professorId) {

        ProfessorModel professor =
                professorService.buscarPorProfessorId(professorId);

        return ResponseEntity.ok(
                ProfessorDTO.fromModel(professor));
    }

    @Operation(description = "Cadastra um novo professor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Professor criado com sucesso"),
            @ApiResponse(responseCode = "400",
                    description = "Não foi possível criar o professor")
    })
    @PostMapping
    public ResponseEntity<ProfessorDTO> criarProfessor(
            @RequestBody ProfessorDTO professorDTO) {

        ProfessorModel novoProfessor =
                professorService.criarProfessor(
                        professorDTO.toModel());

        URI location = URI.create(
                "/api/professores/" +
                        novoProfessor.getProfessorId());

        return ResponseEntity.created(location)
                .body(ProfessorDTO.fromModel(novoProfessor));
    }

    @Operation(description = "Atualiza um professor")
    @PutMapping("/{professorId}")
    public ResponseEntity<ProfessorDTO> atualizarProfessor(
            @PathVariable Long professorId,
            @RequestBody ProfessorDTO professorDTO) {

        ProfessorModel professorAtualizado =
                professorService.atualizarProfessor(
                        professorId,
                        professorDTO.toModel());

        return ResponseEntity.ok(
                ProfessorDTO.fromModel(professorAtualizado));
    }

    @Operation(description = "Exclui um professor")
    @DeleteMapping("/{professorId}")
    public ResponseEntity<Void> deletarProfessor(
            @PathVariable Long professorId) {

        professorService.deletarProfessor(professorId);

        return ResponseEntity.noContent().build();
    }
}
