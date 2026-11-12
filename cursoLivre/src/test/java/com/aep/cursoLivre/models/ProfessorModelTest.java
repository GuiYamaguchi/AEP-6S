package com.aep.cursoLivre.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class ProfessorModelTest {

    private static FormacaoModel formacao() {
        return new FormacaoModel("USP", "Ciencia da Computacao", "Graduacao", 2010);
    }

    private static ProfessorModel criar() {
        return new ProfessorModel(
                "64f1c2b2a1b2c3d4e5f6a7b8",
                3L,
                "Carlos Souza",
                "carlos@email.com",
                "Backend",
                new ArrayList<>(List.of(formacao())));
    }

    @Test
    void deveCriarComConstrutorVazioESetters() {
        ProfessorModel professor = new ProfessorModel();

        assertThat(professor.getId()).isNull();
        assertThat(professor.getFormacoes()).isNull();

        professor.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        professor.setProfessorId(3L);
        professor.setNome("Carlos Souza");
        professor.setEmail("carlos@email.com");
        professor.setEspecialidade("Backend");
        professor.setFormacoes(List.of(formacao()));

        assertThat(professor.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
        assertThat(professor.getProfessorId()).isEqualTo(3L);
        assertThat(professor.getNome()).isEqualTo("Carlos Souza");
        assertThat(professor.getEmail()).isEqualTo("carlos@email.com");
        assertThat(professor.getEspecialidade()).isEqualTo("Backend");
        assertThat(professor.getFormacoes()).containsExactly(formacao());
    }

    @Test
    void deveCriarComConstrutorCompleto() {
        ProfessorModel professor = criar();

        assertThat(professor.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
        assertThat(professor.getProfessorId()).isEqualTo(3L);
        assertThat(professor.getNome()).isEqualTo("Carlos Souza");
        assertThat(professor.getEmail()).isEqualTo("carlos@email.com");
        assertThat(professor.getEspecialidade()).isEqualTo("Backend");
        assertThat(professor.getFormacoes()).hasSize(1);
    }

    @Test
    void deveRespeitarContratoDeEqualsEHashCode() {
        List<Consumer<ProfessorModel>> alteracoes = List.of(
                p -> p.setId("outro-id"),
                p -> p.setProfessorId(999L),
                p -> p.setNome("Outro nome"),
                p -> p.setEmail("outro@email.com"),
                p -> p.setEspecialidade("Frontend"),
                p -> p.setFormacoes(new ArrayList<>()));

        List<Consumer<ProfessorModel>> anulacoes = List.of(
                p -> p.setId(null),
                p -> p.setProfessorId(null),
                p -> p.setNome(null),
                p -> p.setEmail(null),
                p -> p.setEspecialidade(null),
                p -> p.setFormacoes(null));

        ModelContract.verificarEqualsEHashCode(ProfessorModelTest::criar, alteracoes, anulacoes);
    }

    @Test
    void naoDeveLancarExcecaoComTodosOsCamposNulos() {
        ProfessorModel p1 = new ProfessorModel();
        ProfessorModel p2 = new ProfessorModel();

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1.toString()).isNotBlank();
    }

    @Test
    void toStringDeveConterOsDados() {
        assertThat(criar().toString())
                .contains("Carlos Souza")
                .contains("carlos@email.com")
                .contains("Backend")
                .contains("USP");
    }
}
