package com.aep.demo.services.impl;

import com.aep.demo.models.AlunoModel;
import com.aep.demo.repositories.AlunoRepository;
import com.aep.demo.services.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;

    @Override
    public Optional<AlunoModel> getAluno(Long id) {
        return alunoRepository.findById(id);
    }

    @Override
    public AlunoModel postAluno(AlunoModel model) {
        return alunoRepository.save(model);
    }

    @Override
    public void deleteAluno(Long id){
        deleteAluno(id);
    }
}
