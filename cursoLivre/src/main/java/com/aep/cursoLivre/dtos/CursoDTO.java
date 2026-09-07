package com.aep.cursoLivre.dtos;

import com.aep.cursoLivre.models.CursoModel;

public record CursoDTO(
        String id,
        Long cursoId,
        String nome,
        String instituicao,
        String categoria,
        int cargaHoraria,
        String link
) {

    public static CursoDTO fromModel(CursoModel curso) {
        if (curso == null) {
            return null;
        }

        return new CursoDTO(
                curso.getId(),
                curso.getCursoId(),
                curso.getNome(),
                curso.getInstituicao(),
                curso.getCategoria(),
                curso.getCargaHoraria(),
                curso.getLink()
        );
    }

    public CursoModel toModel() {
        CursoModel curso = new CursoModel();

        curso.setNome(nome);
        curso.setInstituicao(instituicao);
        curso.setCategoria(categoria);
        curso.setCargaHoraria(cargaHoraria);
        curso.setLink(link);

        return curso;
    }
}