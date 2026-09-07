package com.aep.cursoLivre.services;

import com.aep.cursoLivre.models.CursoModel;

import java.util.Optional;

public interface CursoService {

    Optional<CursoModel> getCurso(Long id);

    CursoModel postCurso(CursoModel model);

    void deleteCurso(Long id);

}
