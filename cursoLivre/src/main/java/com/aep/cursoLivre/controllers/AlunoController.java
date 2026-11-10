
package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.dtos.AlunoDTO;
import com.aep.cursoLivre.models.AlunoModel;
import com.aep.cursoLivre.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @Operation(description = "Lista todos os alunos")
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarAlunos() {

        List<AlunoDTO> alunos =
                alunoService.listarAlunos()
                        .stream()
                        .map(AlunoDTO::fromModel)
                        .toList();

        return ResponseEntity.ok(alunos);
    }

    @Operation(description = "Busca aluno pelo alunoId")
    @GetMapping("/{alunoId}")
    public ResponseEntity<AlunoDTO> buscarPorAlunoId(
            @PathVariable Long alunoId) {

        AlunoModel aluno =
                alunoService.buscarPorAlunoId(alunoId);

        return ResponseEntity.ok(AlunoDTO.fromModel(aluno));
    }

    @Operation(description = "Cadastra um novo aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Aluno criado com sucesso"),
            @ApiResponse(responseCode = "400",
                    description = "Não foi possível criar o aluno")
    })
    @PostMapping
    public ResponseEntity<AlunoDTO> criarAluno(
            @RequestBody AlunoDTO alunoDTO) {

        AlunoModel novoAluno =
                alunoService.criarAluno(alunoDTO.toModel());

        URI location = URI.create(
                "/api/alunos/" + novoAluno.getAlunoId());

        return ResponseEntity.created(location)
                .body(AlunoDTO.fromModel(novoAluno));
    }

    @Operation(description = "Atualiza os dados de um aluno")
    @PutMapping("/{alunoId}")
    public ResponseEntity<AlunoDTO> atualizarAluno(
            @PathVariable Long alunoId,
            @RequestBody AlunoDTO alunoDTO) {

        AlunoModel alunoAtualizado =
                alunoService.atualizarAluno(
                        alunoId,
                        alunoDTO.toModel());

        return ResponseEntity.ok(
                AlunoDTO.fromModel(alunoAtualizado));
    }

    @Operation(description = "Exclui um aluno")
    @DeleteMapping("/{alunoId}")
    public ResponseEntity<Void> deletarAluno(
            @PathVariable Long alunoId) {

        alunoService.deletarAluno(alunoId);

        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Matricula um aluno em um curso")
    @PostMapping("/{alunoId}/cursos/{cursoId}")
    public ResponseEntity<Void> adicionarCurso(
            @PathVariable Long alunoId,
            @PathVariable Long cursoId) {

        alunoService.adicionarCurso(alunoId, cursoId);

        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Remove a matrícula de um aluno em um curso")
    @DeleteMapping("/{alunoId}/cursos/{cursoId}")
    public ResponseEntity<Void> removerCurso(
            @PathVariable Long alunoId,
            @PathVariable Long cursoId) {

        alunoService.removerCurso(alunoId, cursoId);

        return ResponseEntity.noContent().build();
    }
}
