package com.aep.cursoLivre.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cursos")
public class CursoModel {

    @Id
    private Long id;
    private String nome;
    private String instituicao;
    private String categoria;
    private int cargaHoraria;

}
