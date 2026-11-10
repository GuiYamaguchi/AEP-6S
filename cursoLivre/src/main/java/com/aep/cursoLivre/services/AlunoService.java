
package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.AlunoModel;
import com.aep.cursoLivre.repositories.AlunoRepository;
import com.aep.cursoLivre.repositories.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final GeradorSequencialService sequencialService;

    private static final String ALUNO_SEQUENCE = "aluno_sequence";

    public List<AlunoModel> listarAlunos() {
        try {
            return alunoRepository.findAll();
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao listar alunos: " + e.getMessage(), e);
        }
    }

    public AlunoModel buscarPorAlunoId(Long alunoId) {

        if (alunoId == null) {
            throw new BadRequestException(
                    "O alunoId não pode ser nulo.");
        }

        return alunoRepository.findByAlunoId(alunoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aluno não encontrado: " + alunoId));
    }

    public AlunoModel buscarPorId(String id) {

        if (id == null || id.isBlank()) {
            throw new BadRequestException(
                    "O ID do aluno não pode ser vazio.");
        }

        return alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aluno não encontrado: " + id));
    }

    public AlunoModel criarAluno(AlunoModel aluno) {

        if (aluno == null) {
            throw new BadRequestException(
                    "Os dados do aluno não podem ser nulos.");
        }

        validarCursos(aluno.getCursosMatriculados());

        if (aluno.getCursosMatriculados() == null) {
            aluno.setCursosMatriculados(new ArrayList<>());
        }

        aluno.setAlunoId(
                sequencialService.generateSequence(ALUNO_SEQUENCE));

        try {
            return alunoRepository.save(aluno);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao salvar aluno: " + e.getMessage(), e);
        }
    }

    public AlunoModel atualizarAluno(
            Long alunoId,
            AlunoModel dadosAtualizados) {

        if (dadosAtualizados == null) {
            throw new BadRequestException(
                    "Os dados do aluno não podem ser nulos.");
        }

        validarCursos(dadosAtualizados.getCursosMatriculados());

        AlunoModel aluno = buscarPorAlunoId(alunoId);

        aluno.setNome(dadosAtualizados.getNome());
        aluno.setEmail(dadosAtualizados.getEmail());
        aluno.setDataNascimento(
                dadosAtualizados.getDataNascimento());
        aluno.setCursosMatriculados(
                dadosAtualizados.getCursosMatriculados() == null
                        ? new ArrayList<>()
                        : dadosAtualizados.getCursosMatriculados());

        try {
            return alunoRepository.save(aluno);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao atualizar aluno: " + e.getMessage(), e);
        }
    }

    public void adicionarCurso(Long alunoId, Long cursoId) {

        if (cursoId == null) {
            throw new BadRequestException(
                    "O cursoId não pode ser nulo.");
        }

        AlunoModel aluno = buscarPorAlunoId(alunoId);

        cursoRepository.findByCursoId(cursoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curso não encontrado: " + cursoId));

        if (aluno.getCursosMatriculados() == null) {
            aluno.setCursosMatriculados(new ArrayList<>());
        }

        if (aluno.getCursosMatriculados().contains(cursoId)) {
            throw new BadRequestException(
                    "O aluno já está matriculado neste curso.");
        }

        aluno.getCursosMatriculados().add(cursoId);

        try {
            alunoRepository.save(aluno);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao matricular aluno: " + e.getMessage(), e);
        }
    }

    public void removerCurso(Long alunoId, Long cursoId) {

        if (cursoId == null) {
            throw new BadRequestException(
                    "O cursoId não pode ser nulo.");
        }

        AlunoModel aluno = buscarPorAlunoId(alunoId);

        if (aluno.getCursosMatriculados() == null ||
                !aluno.getCursosMatriculados().contains(cursoId)) {

            throw new ResourceNotFoundException(
                    "Matrícula não encontrada para este aluno.");
        }

        aluno.getCursosMatriculados().remove(cursoId);

        try {
            alunoRepository.save(aluno);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao remover matrícula: " + e.getMessage(), e);
        }
    }

    public void deletarAluno(Long alunoId) {

        AlunoModel aluno = buscarPorAlunoId(alunoId);

        try {
            alunoRepository.delete(aluno);
        } catch (DataAccessException e) {
            throw new ServiceException(
                    "Erro ao deletar aluno: " + e.getMessage(), e);
        }
    }

    private void validarCursos(List<Long> cursos) {

        if (cursos == null) {
            return;
        }

        for (Long cursoId : cursos) {

            if (cursoId == null) {
                throw new BadRequestException(
                        "A lista de cursos não pode conter IDs nulos.");
            }

            if (cursoRepository.findByCursoId(cursoId).isEmpty()) {
                throw new ResourceNotFoundException(
                        "Curso não encontrado: " + cursoId);
            }
        }
    }
}
