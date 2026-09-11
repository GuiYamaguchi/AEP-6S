package com.aep.cursoLivre.dtos;

import com.aep.cursoLivre.models.CursoModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CursoDTOTest {

    @Test
    void deveConverterModelParaDTO() {
        CursoModel model = new CursoModel();
        model.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        model.setCursoId(10L);
        model.setNome("Testes com JUnit 5");
        model.setInstituicao("Alura");
        model.setCategoria("Qualidade de Software");
        model.setCargaHoraria(20);
        model.setLink("http://exemplo.com/junit");

        CursoDTO dto = CursoDTO.fromModel(model);

        assertThat(dto.id()).isEqualTo(model.getId());
        assertThat(dto.cursoId()).isEqualTo(model.getCursoId());
        assertThat(dto.nome()).isEqualTo(model.getNome());
        assertThat(dto.instituicao()).isEqualTo(model.getInstituicao());
        assertThat(dto.categoria()).isEqualTo(model.getCategoria());
        assertThat(dto.cargaHoraria()).isEqualTo(model.getCargaHoraria());
        assertThat(dto.link()).isEqualTo(model.getLink());
    }

    @Test
    void deveRetornarNuloQuandoModelForNulo() {
        assertThat(CursoDTO.fromModel(null)).isNull();
    }

    @Test
    void deveConverterDTOParaModel() {
        CursoDTO dto = new CursoDTO(
                "64f1c2b2a1b2c3d4e5f6a7b8",
                10L,
                "Testes com JUnit 5",
                "Alura",
                "Qualidade de Software",
                20,
                "http://exemplo.com/junit"
        );

        CursoModel model = dto.toModel();

        assertThat(model.getNome()).isEqualTo(dto.nome());
        assertThat(model.getInstituicao()).isEqualTo(dto.instituicao());
        assertThat(model.getCategoria()).isEqualTo(dto.categoria());
        assertThat(model.getCargaHoraria()).isEqualTo(dto.cargaHoraria());
        assertThat(model.getLink()).isEqualTo(dto.link());
    }
}
