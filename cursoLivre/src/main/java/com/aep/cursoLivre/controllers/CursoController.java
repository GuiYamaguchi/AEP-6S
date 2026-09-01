package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping("/cursos")
    public List<CursoModel> getCursos(){
        return cursoService.getCursos();
    }

    @GetMapping("/curso/{id}")
    public CursoModel getCursoPorId(@PathVariable("id")Long id){
        return cursoService.getCursoPorId(id).orElseThrow();
    }

    @PostMapping("/curso")
    public CursoModel postCurso(@RequestBody CursoModel body){
        return cursoService.postCurso(body);
    }

}
