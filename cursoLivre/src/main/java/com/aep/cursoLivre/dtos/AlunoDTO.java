
package com.aep.cursoLivre.dtos;

import com.aep.cursoLivre.models.AlunoModel;

import java.time.LocalDate;
import java.util.List;

public record AlunoDTO(
        String id,
        Long alunoId,
        String nome,
        String email,
        LocalDate dataNascimento,
        List<Long> cursosMatriculados
) {

    public static AlunoDTO fromModel(AlunoModel aluno) {
        if (aluno == null) {
            return null;
        }

        return new AlunoDTO(
                aluno.getId(),
                aluno.getAlunoId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getDataNascimento(),
                aluno.getCursosMatriculados()
        );
    }

    public AlunoModel toModel() {
        AlunoModel aluno = new AlunoModel();

        aluno.setNome(nome);
        aluno.setEmail(email);
        aluno.setDataNascimento(dataNascimento);
        aluno.setCursosMatriculados(cursosMatriculados);

        return aluno;
    }
}
