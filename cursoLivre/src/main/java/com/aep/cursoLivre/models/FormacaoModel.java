package com.aep.cursoLivre.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormacaoModel {

    private String instituicao;
    private String curso;
    private String nivel;
    private int anoConclusao;
}