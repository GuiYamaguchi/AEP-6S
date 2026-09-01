package com.aep.demo.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "alunos")
public class AlunoModel {

    @Id
    private Long id;
    private String nome;
    private String senha;

}
