package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.repositories.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    public List<CursoModel> listarCursos() {
        try {
            return cursoRepository.findAll();
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao buscar a lista de cursos: " + e.getMessage(), e);
        }
    }

    public CursoModel buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            throw new BadRequestException("O ID do curso não pode ser nulo ou vazio.");
        }

        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + id));
    }

    public CursoModel criarCurso(CursoModel curso) {
        if (curso == null) {
            throw new BadRequestException("Os dados do curso não podem ser nulos.");
        }

        try {
            return cursoRepository.save(curso);
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao salvar o curso: " + e.getMessage(), e);
        }
    }

    public CursoModel atualizarCurso(String id, CursoModel cursoAtualizado) {
        if (cursoAtualizado == null) {
            throw new BadRequestException("Os dados do curso não podem ser nulos.");
        }

        CursoModel cursoExistente = buscarPorId(id);

        cursoExistente.setNome(cursoAtualizado.getNome());
        cursoExistente.setInstituicao(cursoAtualizado.getInstituicao());
        cursoExistente.setCategoria(cursoAtualizado.getCategoria());
        cursoExistente.setCargaHoraria(cursoAtualizado.getCargaHoraria());
        cursoExistente.setLink(cursoAtualizado.getLink());

        try {
            return cursoRepository.save(cursoExistente);
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao atualizar o curso: " + e.getMessage(), e);
        }
    }

    public void deletarCurso(String id) {
        CursoModel curso = buscarPorId(id);

        try {
            cursoRepository.delete(curso);
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao deletar o curso: " + e.getMessage(), e);
        }
    }
}