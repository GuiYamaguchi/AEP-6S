package com.aep.demo.controllers;

import com.aep.demo.models.CursoModel;
import com.aep.demo.services.CursoService;
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
