package com.aep.demo.services.impl;

import com.aep.demo.models.CursoModel;
import com.aep.demo.repositories.CursoRepository;
import com.aep.demo.services.CursoService;
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
