package com.aep.cursoLivre.controllers;

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
    public ResponseEntity<List<CursoModel>> listarCursos() {
        List<CursoModel> cursos = cursoService.listarCursos();
        return ResponseEntity.ok(cursos);
    }

    @Operation(description = "Busca o curso pelo seu cursoId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o curso"),
            @ApiResponse(responseCode = "400", description = "Curso não foi encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CursoModel> buscarPorCursoId(@PathVariable Long id) {
        CursoModel curso = cursoService.buscarPorCursoId(id);
        return ResponseEntity.ok(curso);
    }

    @Operation(description = "Busca o curso pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o curso"),
            @ApiResponse(responseCode = "400", description = "Curso não foi encontrado")
    })
    @GetMapping("/mongo/{id}")
    public ResponseEntity<CursoModel> buscarPorId(@PathVariable String id) {
        CursoModel curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(curso);
    }

    @Operation(description = "Chamada para criar curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Curso nao foi possivel ser criado")
    })
    @PostMapping
    public ResponseEntity<CursoModel> criarCurso(@RequestBody CursoModel curso) {
        CursoModel novoCurso = cursoService.criarCurso(curso);
        URI location = URI.create("/api/cursos/" + novoCurso.getId());
        return ResponseEntity.created(location).body(novoCurso);
    }

    @Operation(description = "Chamada para atualizar credenciais de um curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nao foi possivel atualizar o curso")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CursoModel> atualizarCurso(@PathVariable Long id, @RequestBody CursoModel curso) {
        CursoModel cursoAtualizado = cursoService.atualizarCurso(id, curso);
        return ResponseEntity.ok(cursoAtualizado);
    }

    @Operation(description = "Chamada para deletar um curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nao foi possivel deletar o curso")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCurso(@PathVariable String id) {
        cursoService.deletarCurso(id);
        return ResponseEntity.noContent().build();
    }
}
