package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.dtos.CursoDTO;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Gerenciamento de cursos gratuitos")
public class CursoController {

    private final CursoService cursoService;

    // Listar todos os cursos
    @GetMapping
    @Operation(summary = "Listar todos os cursos")
    public ResponseEntity<List<CursoDTO>> listarCursos() {

        List<CursoDTO> cursos = cursoService.listarCursos()
                .stream()
                .map(CursoDTO::fromModel)
                .toList();

        return ResponseEntity.ok(cursos);
    }

    // Buscar curso pelo ID do MongoDB
    @GetMapping("/{id}")
    @Operation(summary = "Buscar curso pelo ID")
    public ResponseEntity<CursoDTO> buscarPorId(@PathVariable String id) {

        CursoModel curso = cursoService.buscarPorId(id);

        return ResponseEntity.ok(CursoDTO.fromModel(curso));
    }

    // Buscar curso pelo ID numérico
    @GetMapping("/curso-id/{cursoId}")
    @Operation(summary = "Buscar curso pelo código numérico")
    public ResponseEntity<CursoDTO> buscarPorCursoId(
            @PathVariable Long cursoId) {

        CursoModel curso = cursoService.buscarPorCursoId(cursoId);

        return ResponseEntity.ok(CursoDTO.fromModel(curso));
    }

    // Cadastrar curso
    @PostMapping
    @Operation(summary = "Cadastrar um novo curso")
    public ResponseEntity<CursoDTO> criarCurso(
            @RequestBody CursoDTO cursoDTO) {

        CursoModel curso = cursoDTO.toModel();

        CursoModel cursoCriado = cursoService.criarCurso(curso);

        URI location = URI.create(
                "/api/cursos/" + cursoCriado.getId()
        );

        return ResponseEntity
                .created(location)
                .body(CursoDTO.fromModel(cursoCriado));
    }

    // Atualizar curso
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um curso")
    public ResponseEntity<CursoDTO> atualizarCurso(
            @PathVariable String id,
            @RequestBody CursoDTO cursoDTO) {

        CursoModel curso = cursoDTO.toModel();

        CursoModel cursoAtualizado =
                cursoService.atualizarCurso(id, curso);

        return ResponseEntity.ok(
                CursoDTO.fromModel(cursoAtualizado)
        );
    }

    // Excluir curso
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um curso")
    public ResponseEntity<Void> deletarCurso(
            @PathVariable String id) {

        cursoService.deletarCurso(id);

        return ResponseEntity.noContent().build();
    }
}