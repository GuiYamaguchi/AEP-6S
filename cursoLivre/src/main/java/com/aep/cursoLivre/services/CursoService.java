package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.repositories.CursoRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    private static final String CURSO_SEQUENCE = "curso_sequence";

    private final CursoRepository cursoRepository;
    private final GeradorSequencialService sequenceGeneratorService;

    public CursoService(
            CursoRepository cursoRepository,
            GeradorSequencialService sequenceGeneratorService) {
        this.cursoRepository = cursoRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    // Listar todos os cursos
    public List<CursoModel> listarCursos() {
        try {
            return cursoRepository.findAll();
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao listar os cursos.", e);
        }
    }

    // Buscar curso pelo ID do MongoDB
    public CursoModel buscarPorId(String id) {

        if (id == null || id.isBlank()) {
            throw new BadRequestException("O ID do curso é obrigatório.");
        }

        try {
            return cursoRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Curso não encontrado com o ID: " + id
                            )
                    );
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao buscar o curso.", e);
        }
    }

    public CursoModel buscarPorCursoId(Long cursoId) {

        if (cursoId == null || cursoId <= 0) {
            throw new BadRequestException("O código do curso é inválido.");
        }

        try {
            return cursoRepository.findByCursoId(cursoId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Curso não encontrado com o código: " + cursoId
                            )
                    );
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao buscar o curso.", e);
        }
    }

    public CursoModel criarCurso(CursoModel curso) {

        validarCurso(curso);

        try {
            curso.setCursoId(
                    sequenceGeneratorService.generateSequence(CURSO_SEQUENCE)
            );

            return cursoRepository.save(curso);

        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao cadastrar o curso.", e);
        }
    }

    public CursoModel atualizarCurso(String id, CursoModel cursoAtualizado) {

        if (id == null || id.isBlank()) {
            throw new BadRequestException("O ID do curso é obrigatório.");
        }

        validarCurso(cursoAtualizado);

        try {
            CursoModel cursoExistente = cursoRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Curso não encontrado com o ID: " + id
                            )
                    );

            cursoExistente.setNome(cursoAtualizado.getNome());
            cursoExistente.setInstituicao(cursoAtualizado.getInstituicao());
            cursoExistente.setCategoria(cursoAtualizado.getCategoria());
            cursoExistente.setCargaHoraria(cursoAtualizado.getCargaHoraria());
            cursoExistente.setLink(cursoAtualizado.getLink());

            return cursoRepository.save(cursoExistente);

        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao atualizar o curso.", e);
        }
    }

    public void deletarCurso(String id) {

        if (id == null || id.isBlank()) {
            throw new BadRequestException("O ID do curso é obrigatório.");
        }

        try {
            CursoModel curso = cursoRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Curso não encontrado com o ID: " + id
                            )
                    );

            cursoRepository.delete(curso);

        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao excluir o curso.", e);
        }
    }

    private void validarCurso(CursoModel curso) {

        if (curso == null) {
            throw new BadRequestException("Os dados do curso são obrigatórios.");
        }

        if (curso.getNome() == null || curso.getNome().isBlank()) {
            throw new BadRequestException("O nome do curso é obrigatório.");
        }

        if (curso.getInstituicao() == null || curso.getInstituicao().isBlank()) {
            throw new BadRequestException("A instituição é obrigatória.");
        }

        if (curso.getCategoria() == null || curso.getCategoria().isBlank()) {
            throw new BadRequestException("A categoria é obrigatória.");
        }

        if (curso.getCargaHoraria() <= 0) {
            throw new BadRequestException("A carga horária deve ser maior que zero.");
        }

        if (curso.getLink() == null || curso.getLink().isBlank()) {
            throw new BadRequestException("O link do curso é obrigatório.");
        }
    }
}