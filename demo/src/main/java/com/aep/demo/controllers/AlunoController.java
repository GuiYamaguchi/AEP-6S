package com.aep.demo.controllers;

import com.aep.demo.models.AlunoModel;
import com.aep.demo.services.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping(value = "/aluno/{id}")
    AlunoModel getUser(@PathVariable("id")Long id){
        return alunoService.getAluno(id).orElseThrow();
    }

    @PostMapping(value = "/aluno")
    AlunoModel postAluno(@RequestBody AlunoModel body){
        return alunoService.postAluno(body);
    }

}
