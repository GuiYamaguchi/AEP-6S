
package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.ProfessorModel;
import com.aep.cursoLivre.repositories.CursoRepository;
import com.aep.cursoLivre.repositories.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final GeradorSequencialService sequencialService;

    private static final String PROFESSOR_SEQUENCE =
            "professor_sequence";

    public List<ProfessorModel> listarProfessores() {
        try {
            return professorRepository.findAll();
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao listar professores: " + e.getMessage(), e);
        }
    }

    public ProfessorModel buscarPorProfessorId(Long professorId) {

        if (professorId == null) {
            throw new BadRequestException(
                    "O professorId não pode ser nulo.");
        }

        return professorRepository.findByProfessorId(professorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professor não encontrado: " + professorId));
    }

    public ProfessorModel buscarPorId(String id) {

        if (id == null || id.isBlank()) {
            throw new BadRequestException(
                    "O ID do professor não pode ser vazio.");
        }

        return professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professor não encontrado: " + id));
    }

    public ProfessorModel criarProfessor(ProfessorModel professor) {

        if (professor == null) {
            throw new BadRequestException(
                    "Os dados do professor não podem ser nulos.");
        }

        professor.setProfessorId(
                sequencialService.generateSequence(PROFESSOR_SEQUENCE));

        try {
            return professorRepository.save(professor);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao salvar professor: " + e.getMessage(), e);
        }
    }

    public ProfessorModel atualizarProfessor(
            Long professorId,
            ProfessorModel dadosAtualizados) {

        if (dadosAtualizados == null) {
            throw new BadRequestException(
                    "Os dados do professor não podem ser nulos.");
        }

        ProfessorModel professor =
                buscarPorProfessorId(professorId);

        professor.setNome(dadosAtualizados.getNome());
        professor.setEmail(dadosAtualizados.getEmail());
        professor.setEspecialidade(
                dadosAtualizados.getEspecialidade());
        professor.setFormacoes(dadosAtualizados.getFormacoes());

        try {
            return professorRepository.save(professor);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao atualizar professor: " + e.getMessage(), e);
        }
    }

    public void deletarProfessor(Long professorId) {

        ProfessorModel professor =
                buscarPorProfessorId(professorId);

        if (!cursoRepository.findByProfessorId(professorId).isEmpty()) {
            throw new BadRequestException(
                    "Não é possível excluir um professor que possui cursos.");
        }

        try {
            professorRepository.delete(professor);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao deletar professor: " + e.getMessage(), e);
        }
    }

    private void validarProfessor(Long professorId) {

        if (professorId == null) {
            throw new BadRequestException(
                    "O professorId é obrigatório.");
        }

        if (professorRepository.findByProfessorId(professorId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Professor não encontrado: " + professorId);
        }
    }
}
