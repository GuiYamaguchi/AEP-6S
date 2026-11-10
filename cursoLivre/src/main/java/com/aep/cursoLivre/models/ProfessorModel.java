package com.aep.cursoLivre.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "professores")
public class ProfessorModel{
    @Id
    private String id;

    private Long professorId;
    private String nome;
    private String email;
    private String especialidade;

    private List<FormacaoModel> formacoes;
}