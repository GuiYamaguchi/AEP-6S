
package com.aep.cursoLivre.dtos;

import com.aep.cursoLivre.models.FormacaoModel;
import com.aep.cursoLivre.models.ProfessorModel;

import java.util.List;

public record ProfessorDTO(
        String id,
        Long professorId,
        String nome,
        String email,
        String especialidade,
        List<FormacaoModel> formacoes
) {

    public static ProfessorDTO fromModel(ProfessorModel professor) {
        if (professor == null) {
            return null;
        }

        return new ProfessorDTO(
                professor.getId(),
                professor.getProfessorId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getEspecialidade(),
                professor.getFormacoes()
        );
    }

    public ProfessorModel toModel() {
        ProfessorModel professor = new ProfessorModel();

        professor.setNome(nome);
        professor.setEmail(email);
        professor.setEspecialidade(especialidade);
        professor.setFormacoes(formacoes);

        return professor;
    }
}
