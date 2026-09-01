package com.aep.cursoLivre.services.impl;

import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.repositories.CursoRepository;
import com.aep.cursoLivre.services.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    @Override
    public Optional<CursoModel> getCurso(Long id) {
        return cursoRepository.findById(id);
    }

    @Override
    public CursoModel postCurso(CursoModel model) {
        return cursoRepository.save(model);
    }

    @Override
    public void deleteCurso(Long id){
        deleteCurso(id);
    }
}
