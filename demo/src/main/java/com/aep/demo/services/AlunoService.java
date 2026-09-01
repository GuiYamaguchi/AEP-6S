package com.aep.demo.services;

import com.aep.demo.models.AlunoModel;

import java.util.Optional;

public interface AlunoService {

    Optional<AlunoModel> getAluno(Long id);

    AlunoModel postAluno(AlunoModel model);

    void deleteAluno(Long id);

}
