package com.aep.cursoLivre.models;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Por estarmos usando o Lombok (Arrependimento) nossos testes necessitaram ficar redundante
 * para chegar aos 70% de coberturas necessários, visto que, o @data, cria além dos getters e setter os toHash, toString e o toEqual
 * que o JaCoCo reconhece e cobra
 * */
class DatabaseSequenceModelTest {

    @Nested
    class ConstrutoresEGettersSetters {

        @Test
        void deveCriarComConstrutorVazioESetters() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel();
            sequence.setId("curso_sequence");
            sequence.setSeq(42L);

            assertThat(sequence.getId()).isEqualTo("curso_sequence");
            assertThat(sequence.getSeq()).isEqualTo(42L);
        }

        @Test
        void deveCriarComConstrutorCompleto() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(sequence.getId()).isEqualTo("curso_sequence");
            assertThat(sequence.getSeq()).isEqualTo(7L);
        }
    }

    @Nested
    class Equals {

        @Test
        void deveSerIgualAoComparadoComEleMesmo() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(sequence).isEqualTo(sequence);
            assertThat(sequence.equals(sequence)).isTrue();
        }

        @Test
        void naoDeveSerIgualANulo() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(sequence).isNotEqualTo(null);
            assertThat(sequence.equals(null)).isFalse();
        }

        @Test
        void naoDeveSerIgualAObjetoDeOutraClasse() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(sequence).isNotEqualTo("uma string qualquer");
            assertThat(sequence.canEqual("uma string qualquer")).isFalse();
        }

        @Test
        void duasSequenciasComMesmosDadosDevemSerIguais() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel("curso_sequence", 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(s1).isEqualTo(s2);
            assertThat(s1.canEqual(s2)).isTrue();
        }

        @Test
        void sequenciasComIdsDiferentesNaoDevemSerIguais() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel("curso_sequence", 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel("outra_sequence", 7L);

            assertThat(s1).isNotEqualTo(s2);
        }

        @Test
        void sequenciasComSeqDiferenteNaoDevemSerIguais() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel("curso_sequence", 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel("curso_sequence", 8L);

            assertThat(s1).isNotEqualTo(s2);
        }

        @Test
        void sequenciasComIdNuloDevemSerIguaisSeOutraTambemForNula() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel(null, 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel(null, 7L);

            assertThat(s1).isEqualTo(s2);
        }

        @Test
        void sequenciaComIdNuloNaoDeveSerIgualASequenciaComIdPreenchido() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel(null, 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(s1).isNotEqualTo(s2);
            assertThat(s2).isNotEqualTo(s1);
        }
    }

    @Nested
    class HashCode {

        @Test
        void duasSequenciasIguaisDevemTerOMesmoHashCode() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel("curso_sequence", 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        }

        @Test
        void sequenciasDiferentesDevemTerHashCodesDiferentes() {
            DatabaseSequenceModel s1 = new DatabaseSequenceModel("curso_sequence", 7L);
            DatabaseSequenceModel s2 = new DatabaseSequenceModel("outra_sequence", 99L);

            assertThat(s1.hashCode()).isNotEqualTo(s2.hashCode());
        }

        @Test
        void naoDeveLancarExcecaoAoCalcularHashCodeComIdNulo() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel(null, 0L);

            assertThat(sequence.hashCode()).isNotNull();
        }
    }

    @Nested
    class ToString {

        @Test
        void deveConterOsDadosDaSequencia() {
            DatabaseSequenceModel sequence = new DatabaseSequenceModel("curso_sequence", 7L);

            assertThat(sequence.toString())
                    .isNotBlank()
                    .contains("curso_sequence")
                    .contains("7");
        }
    }
}