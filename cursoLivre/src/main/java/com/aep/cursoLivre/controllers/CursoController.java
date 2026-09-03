package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.dtos.CursoDTO;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;
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

    @GetMapping
    public ResponseEntity<List<CursoDTO>> listarCursos() {
        List<CursoDTO> cursos = cursoService.listarCursos()
                .stream()
                .map(CursoDTO::fromModel)
                .toList();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{cursoId}")
    public ResponseEntity<CursoDTO> buscarPorCursoId(@PathVariable Long cursoId) {
        CursoModel curso = cursoService.buscarPorCursoId(cursoId);
        return ResponseEntity.ok(CursoDTO.fromModel(curso));
    }

    @GetMapping("/mongo/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(@PathVariable String id) {
        CursoModel curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(CursoDTO.fromModel(curso));
    }

    @PostMapping
    public ResponseEntity<CursoDTO> criarCurso(@RequestBody CursoDTO cursoDTO) {
        CursoModel novoCurso = cursoService.criarCurso(cursoDTO.toModel());
        URI location = URI.create("/api/cursos/" + novoCurso.getId());
        return ResponseEntity.created(location).body(CursoDTO.fromModel(novoCurso));
    }

    @PutMapping("/{cursoId}")
    public ResponseEntity<CursoDTO> atualizarCurso(@PathVariable Long cursoId, @RequestBody CursoDTO cursoDTO) {
        CursoModel cursoAtualizado = cursoService.atualizarCurso(cursoId, cursoDTO.toModel());
        return ResponseEntity.ok(CursoDTO.fromModel(cursoAtualizado));
    }

    @DeleteMapping("/{cursoId}")
    public ResponseEntity<Void> deletarCurso(@PathVariable Long cursoId) {
        cursoService.deletarCurso(cursoId);
        return ResponseEntity.noContent().build();
    }
}
