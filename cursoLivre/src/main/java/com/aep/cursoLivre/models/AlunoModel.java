package com.aep.cursoLivre.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alunos")
public class AlunoModel {

    @Id
    private String id;

    private Long alunoId;
    private String nome;
    private String email;
    private LocalDate dataNascimento;

    private List<Long> cursosMatriculados;
}