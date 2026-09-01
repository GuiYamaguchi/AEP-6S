package com.aep.demo.services;

import com.aep.demo.models.CursoModel;

import java.util.Optional;

public interface CursoService {

    Optional<CursoModel> getCurso(Long id);

    CursoModel postCurso(CursoModel model);

    void deleteCurso(Long id);

}
