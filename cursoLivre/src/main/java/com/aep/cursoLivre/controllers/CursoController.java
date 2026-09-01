package com.aep.cursoLivre.controllers;

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
    public ResponseEntity<List<CursoModel>> listarCursos() {
        List<CursoModel> cursos = cursoService.listarCursos();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoModel> buscarPorId(@PathVariable String id) {
        CursoModel curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(curso);
    }

    @PostMapping
    public ResponseEntity<CursoModel> criarCurso(@RequestBody CursoModel curso) {
        CursoModel novoCurso = cursoService.criarCurso(curso);
        URI location = URI.create("/api/cursos/" + novoCurso.getId());
        return ResponseEntity.created(location).body(novoCurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoModel> atualizarCurso(@PathVariable String id, @RequestBody CursoModel curso) {
        CursoModel cursoAtualizado = cursoService.atualizarCurso(id, curso);
        return ResponseEntity.ok(cursoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCurso(@PathVariable String id) {
        cursoService.deletarCurso(id);
        return ResponseEntity.noContent().build();
    }
}
