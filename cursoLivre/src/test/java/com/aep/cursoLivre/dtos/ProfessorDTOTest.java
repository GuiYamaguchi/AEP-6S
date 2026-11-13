package com.aep.cursoLivre.dtos;

import com.aep.cursoLivre.models.FormacaoModel;
import com.aep.cursoLivre.models.ProfessorModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProfessorDTOTest {

    private final FormacaoModel formacao =
            new FormacaoModel("USP", "Ciencia da Computacao", "Graduacao", 2010);

    @Test
    void deveConverterModelParaDTO() {
        ProfessorModel model = new ProfessorModel(
                "64f1c2b2a1b2c3d4e5f6a7b8",
                3L,
                "Carlos Souza",
                "carlos@email.com",
                "Backend",
                List.of(formacao));

        ProfessorDTO dto = ProfessorDTO.fromModel(model);

        assertThat(dto.id()).isEqualTo(model.getId());
        assertThat(dto.professorId()).isEqualTo(model.getProfessorId());
        assertThat(dto.nome()).isEqualTo(model.getNome());
        assertThat(dto.email()).isEqualTo(model.getEmail());
        assertThat(dto.especialidade()).isEqualTo(model.getEspecialidade());
        assertThat(dto.formacoes()).containsExactly(formacao);
    }

    @Test
    void deveRetornarNuloQuandoModelForNulo() {
        assertThat(ProfessorDTO.fromModel(null)).isNull();
    }

    @Test
    void deveConverterDTOParaModel() {
        ProfessorDTO dto = new ProfessorDTO(
                "id-do-cliente",
                99L,
                "Carlos Souza",
                "carlos@email.com",
                "Backend",
                List.of(formacao));

        ProfessorModel model = dto.toModel();

        assertThat(model.getNome()).isEqualTo("Carlos Souza");
        assertThat(model.getEmail()).isEqualTo("carlos@email.com");
        assertThat(model.getEspecialidade()).isEqualTo("Backend");
        assertThat(model.getFormacoes()).containsExactly(formacao);
    }

    @Test
    void toModelNaoDeveCopiarIdsGeradosPeloSistema() {
        ProfessorDTO dto = new ProfessorDTO("id-do-cliente", 99L, "Carlos", "c@e.com", "Backend", null);

        ProfessorModel model = dto.toModel();

        assertThat(model.getId()).isNull();
        assertThat(model.getProfessorId()).isNull();
        assertThat(model.getFormacoes()).isNull();
    }
}
