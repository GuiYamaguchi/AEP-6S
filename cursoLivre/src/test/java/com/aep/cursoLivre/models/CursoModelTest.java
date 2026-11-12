package com.aep.cursoLivre.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Por estarmos usando o Lombok, o @Data cria alem de getters e setters os metodos
 * equals, hashCode e toString, que o JaCoCo reconhece e cobra. Por isso o equals/hashCode
 * e verificado campo a campo (ver ModelContract).
 */
class CursoModelTest {

    private static CursoModel criar() {
        return new CursoModel(
                "64f1c2b2a1b2c3d4e5f6a7b8",
                100L,
                "Docker na Pratica",
                "DIO",
                "DevOps",
                30,
                "http://exemplo.com/docker",
                7L);
    }

    @Test
    void deveCriarComConstrutorVazioESetters() {
        CursoModel curso = new CursoModel();

        assertThat(curso.getId()).isNull();
        assertThat(curso.getCursoId()).isNull();
        assertThat(curso.getCargaHoraria()).isZero();

        curso.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        curso.setCursoId(100L);
        curso.setNome("Docker na Pratica");
        curso.setInstituicao("DIO");
        curso.setCategoria("DevOps");
        curso.setCargaHoraria(30);
        curso.setLink("http://exemplo.com/docker");
        curso.setProfessorId(7L);

        assertThat(curso.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
        assertThat(curso.getCursoId()).isEqualTo(100L);
        assertThat(curso.getNome()).isEqualTo("Docker na Pratica");
        assertThat(curso.getInstituicao()).isEqualTo("DIO");
        assertThat(curso.getCategoria()).isEqualTo("DevOps");
        assertThat(curso.getCargaHoraria()).isEqualTo(30);
        assertThat(curso.getLink()).isEqualTo("http://exemplo.com/docker");
        assertThat(curso.getProfessorId()).isEqualTo(7L);
    }

    @Test
    void deveCriarComConstrutorCompleto() {
        CursoModel curso = criar();

        assertThat(curso.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
        assertThat(curso.getCursoId()).isEqualTo(100L);
        assertThat(curso.getNome()).isEqualTo("Docker na Pratica");
        assertThat(curso.getInstituicao()).isEqualTo("DIO");
        assertThat(curso.getCategoria()).isEqualTo("DevOps");
        assertThat(curso.getCargaHoraria()).isEqualTo(30);
        assertThat(curso.getLink()).isEqualTo("http://exemplo.com/docker");
        assertThat(curso.getProfessorId()).isEqualTo(7L);
    }

    @Test
    void deveRespeitarContratoDeEqualsEHashCode() {
        List<Consumer<CursoModel>> alteracoes = List.of(
                c -> c.setId("outro-id"),
                c -> c.setCursoId(999L),
                c -> c.setNome("Outro nome"),
                c -> c.setInstituicao("Outra instituicao"),
                c -> c.setCategoria("Outra categoria"),
                c -> c.setCargaHoraria(999),
                c -> c.setLink("http://outro.com"),
                c -> c.setProfessorId(999L));

        List<Consumer<CursoModel>> anulacoes = List.of(
                c -> c.setId(null),
                c -> c.setCursoId(null),
                c -> c.setNome(null),
                c -> c.setInstituicao(null),
                c -> c.setCategoria(null),
                c -> c.setLink(null),
                c -> c.setProfessorId(null));

        ModelContract.verificarEqualsEHashCode(CursoModelTest::criar, alteracoes, anulacoes);
    }

    @Test
    void naoDeveLancarExcecaoComTodosOsCamposNulos() {
        CursoModel c1 = new CursoModel();
        CursoModel c2 = new CursoModel();

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1.toString()).isNotBlank();
    }

    @Test
    void toStringDeveConterOsDados() {
        assertThat(criar().toString())
                .contains("Docker na Pratica")
                .contains("DIO")
                .contains("DevOps")
                .contains("100")
                .contains("7");
    }
}
