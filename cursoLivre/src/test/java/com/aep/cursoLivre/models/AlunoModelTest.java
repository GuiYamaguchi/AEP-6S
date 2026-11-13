package com.aep.cursoLivre.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class AlunoModelTest {

    private static AlunoModel criar() {
        return new AlunoModel(
                "64f1c2b2a1b2c3d4e5f6a7b8",
                1L,
                "Maria Silva",
                "maria@email.com",
                LocalDate.of(2000, 5, 20),
                new ArrayList<>(List.of(10L, 20L)));
    }

    @Test
    void deveCriarComConstrutorVazioESetters() {
        AlunoModel aluno = new AlunoModel();

        assertThat(aluno.getId()).isNull();
        assertThat(aluno.getCursosMatriculados()).isNull();

        aluno.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        aluno.setAlunoId(1L);
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setDataNascimento(LocalDate.of(2000, 5, 20));
        aluno.setCursosMatriculados(List.of(10L));

        assertThat(aluno.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
        assertThat(aluno.getAlunoId()).isEqualTo(1L);
        assertThat(aluno.getNome()).isEqualTo("Maria Silva");
        assertThat(aluno.getEmail()).isEqualTo("maria@email.com");
        assertThat(aluno.getDataNascimento()).isEqualTo(LocalDate.of(2000, 5, 20));
        assertThat(aluno.getCursosMatriculados()).containsExactly(10L);
    }

    @Test
    void deveCriarComConstrutorCompleto() {
        AlunoModel aluno = criar();

        assertThat(aluno.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
        assertThat(aluno.getAlunoId()).isEqualTo(1L);
        assertThat(aluno.getNome()).isEqualTo("Maria Silva");
        assertThat(aluno.getEmail()).isEqualTo("maria@email.com");
        assertThat(aluno.getDataNascimento()).isEqualTo(LocalDate.of(2000, 5, 20));
        assertThat(aluno.getCursosMatriculados()).containsExactly(10L, 20L);
    }

    @Test
    void deveRespeitarContratoDeEqualsEHashCode() {
        List<Consumer<AlunoModel>> alteracoes = List.of(
                a -> a.setId("outro-id"),
                a -> a.setAlunoId(999L),
                a -> a.setNome("Outro nome"),
                a -> a.setEmail("outro@email.com"),
                a -> a.setDataNascimento(LocalDate.of(1990, 1, 1)),
                a -> a.setCursosMatriculados(new ArrayList<>(List.of(99L))));

        List<Consumer<AlunoModel>> anulacoes = List.of(
                a -> a.setId(null),
                a -> a.setAlunoId(null),
                a -> a.setNome(null),
                a -> a.setEmail(null),
                a -> a.setDataNascimento(null),
                a -> a.setCursosMatriculados(null));

        ModelContract.verificarEqualsEHashCode(AlunoModelTest::criar, alteracoes, anulacoes);
    }

    @Test
    void naoDeveLancarExcecaoComTodosOsCamposNulos() {
        AlunoModel a1 = new AlunoModel();
        AlunoModel a2 = new AlunoModel();

        assertThat(a1).isEqualTo(a2);
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
        assertThat(a1.toString()).isNotBlank();
    }

    @Test
    void toStringDeveConterOsDados() {
        assertThat(criar().toString())
                .contains("Maria Silva")
                .contains("maria@email.com")
                .contains("2000-05-20")
                .contains("10")
                .contains("20");
    }
}
