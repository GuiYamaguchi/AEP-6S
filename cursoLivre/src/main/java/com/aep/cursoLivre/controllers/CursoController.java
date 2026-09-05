package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.dtos.CursoDTO;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @Operation(description = "Lista todos os cursos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todos os cursos são listados"),
            @ApiResponse(responseCode = "400", description = "Houve um erro de syntax/payload")
    })
    @GetMapping
    public ResponseEntity<List<CursoDTO>> listarCursos() {
        List<CursoDTO> cursos = cursoService.listarCursos()
                .stream()
                .map(CursoDTO::fromModel)
                .toList();
        return ResponseEntity.ok(cursos);
    }

    @Operation(description = "Busca o curso pelo seu cursoId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o curso"),
            @ApiResponse(responseCode = "400", description = "Curso não foi encontrado")
    })
    @GetMapping("/{cursoId}")
    public ResponseEntity<CursoDTO> buscarPorCursoId(@PathVariable Long cursoId) {
        CursoModel curso = cursoService.buscarPorCursoId(cursoId);
        return ResponseEntity.ok(CursoDTO.fromModel(curso));
    }

    @Operation(description = "Busca o curso pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o curso"),
            @ApiResponse(responseCode = "400", description = "Curso não foi encontrado")
    })
    @GetMapping("/mongo/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(@PathVariable String id) {
        CursoModel curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(CursoDTO.fromModel(curso));
    }

    @Operation(description = "Chamada para criar curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Curso nao foi possivel ser criado")
    })
    @PostMapping
    public ResponseEntity<CursoDTO> criarCurso(@RequestBody CursoDTO cursoDTO) {
        CursoModel novoCurso = cursoService.criarCurso(cursoDTO.toModel());
        URI location = URI.create("/api/cursos/" + novoCurso.getId());
        return ResponseEntity.created(location).body(CursoDTO.fromModel(novoCurso));
    }

    @Operation(description = "Chamada para atualizar credenciais de um curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nao foi possivel atualizar o curso")
    })
    @PutMapping("/{cursoId}")
    public ResponseEntity<CursoDTO> atualizarCurso(@PathVariable Long cursoId, @RequestBody CursoDTO cursoDTO) {
        CursoModel cursoAtualizado = cursoService.atualizarCurso(cursoId, cursoDTO.toModel());
        return ResponseEntity.ok(CursoDTO.fromModel(cursoAtualizado));
    }

    @Operation(description = "Chamada para deletar um curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nao foi possivel deletar o curso")
    })
    @DeleteMapping("/{cursoId}")
    public ResponseEntity<Void> deletarCurso(@PathVariable Long cursoId) {
        cursoService.deletarCurso(cursoId);
        return ResponseEntity.noContent().build();
    }
}
