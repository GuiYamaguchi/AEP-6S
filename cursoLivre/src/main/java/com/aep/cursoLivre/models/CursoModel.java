package com.aep.cursoLivre.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cursos")
public class CursoModel {

    @Id
    private String id;
    private String nome;
    private String instituicao;
    private String categoria;
    private int cargaHoraria;
    private String link;

}
