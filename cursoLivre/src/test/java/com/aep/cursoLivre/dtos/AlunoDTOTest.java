package com.aep.cursoLivre.dtos;

import com.aep.cursoLivre.models.AlunoModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlunoDTOTest {

    @Test
    void deveConverterModelParaDTO() {
        AlunoModel model = new AlunoModel(
                "64f1c2b2a1b2c3d4e5f6a7b8",
                1L,
                "Maria Silva",
                "maria@email.com",
                LocalDate.of(2000, 5, 20),
                List.of(10L, 20L));

        AlunoDTO dto = AlunoDTO.fromModel(model);

        assertThat(dto.id()).isEqualTo(model.getId());
        assertThat(dto.alunoId()).isEqualTo(model.getAlunoId());
        assertThat(dto.nome()).isEqualTo(model.getNome());
        assertThat(dto.email()).isEqualTo(model.getEmail());
        assertThat(dto.dataNascimento()).isEqualTo(model.getDataNascimento());
        assertThat(dto.cursosMatriculados()).containsExactly(10L, 20L);
    }

    @Test
    void deveRetornarNuloQuandoModelForNulo() {
        assertThat(AlunoDTO.fromModel(null)).isNull();
    }

    @Test
    void deveConverterDTOParaModel() {
        AlunoDTO dto = new AlunoDTO(
                "id-do-cliente",
                99L,
                "Maria Silva",
                "maria@email.com",
                LocalDate.of(2000, 5, 20),
                List.of(10L));

        AlunoModel model = dto.toModel();

        assertThat(model.getNome()).isEqualTo("Maria Silva");
        assertThat(model.getEmail()).isEqualTo("maria@email.com");
        assertThat(model.getDataNascimento()).isEqualTo(LocalDate.of(2000, 5, 20));
        assertThat(model.getCursosMatriculados()).containsExactly(10L);
    }

    @Test
    void toModelNaoDeveCopiarIdsGeradosPeloSistema() {
        AlunoDTO dto = new AlunoDTO("id-do-cliente", 99L, "Maria", "m@e.com", null, null);

        AlunoModel model = dto.toModel();

        assertThat(model.getId()).isNull();
        assertThat(model.getAlunoId()).isNull();
        assertThat(model.getCursosMatriculados()).isNull();
    }
}
