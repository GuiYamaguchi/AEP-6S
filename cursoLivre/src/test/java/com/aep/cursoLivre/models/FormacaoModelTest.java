package com.aep.cursoLivre.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class FormacaoModelTest {

    private static FormacaoModel criar() {
        return new FormacaoModel("USP", "Ciencia da Computacao", "Graduacao", 2010);
    }

    @Test
    void deveCriarComConstrutorVazioESetters() {
        FormacaoModel formacao = new FormacaoModel();

        assertThat(formacao.getInstituicao()).isNull();
        assertThat(formacao.getAnoConclusao()).isZero();

        formacao.setInstituicao("USP");
        formacao.setCurso("Ciencia da Computacao");
        formacao.setNivel("Graduacao");
        formacao.setAnoConclusao(2010);

        assertThat(formacao.getInstituicao()).isEqualTo("USP");
        assertThat(formacao.getCurso()).isEqualTo("Ciencia da Computacao");
        assertThat(formacao.getNivel()).isEqualTo("Graduacao");
        assertThat(formacao.getAnoConclusao()).isEqualTo(2010);
    }

    @Test
    void deveCriarComConstrutorCompleto() {
        FormacaoModel formacao = criar();

        assertThat(formacao.getInstituicao()).isEqualTo("USP");
        assertThat(formacao.getCurso()).isEqualTo("Ciencia da Computacao");
        assertThat(formacao.getNivel()).isEqualTo("Graduacao");
        assertThat(formacao.getAnoConclusao()).isEqualTo(2010);
    }

    @Test
    void deveRespeitarContratoDeEqualsEHashCode() {
        List<Consumer<FormacaoModel>> alteracoes = List.of(
                f -> f.setInstituicao("UNICAMP"),
                f -> f.setCurso("Engenharia"),
                f -> f.setNivel("Mestrado"),
                f -> f.setAnoConclusao(1999));

        List<Consumer<FormacaoModel>> anulacoes = List.of(
                f -> f.setInstituicao(null),
                f -> f.setCurso(null),
                f -> f.setNivel(null));

        ModelContract.verificarEqualsEHashCode(FormacaoModelTest::criar, alteracoes, anulacoes);
    }

    @Test
    void toStringDeveConterOsDados() {
        assertThat(criar().toString())
                .contains("USP")
                .contains("Ciencia da Computacao")
                .contains("Graduacao")
                .contains("2010");
    }
}
