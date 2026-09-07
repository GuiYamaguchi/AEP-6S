package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping(value = "/curso/{id}")
    CursoModel getUser(@PathVariable("id")Long id){
        return cursoService.getCurso(id).orElseThrow();
    }

    @PostMapping(value = "/curso")
    CursoModel postAluno(@RequestBody CursoModel body){
        return cursoService.postCurso(body);
    }

}
