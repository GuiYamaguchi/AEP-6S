package com.aep.cursoLivre.models;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
/*
* Por estarmos usando o Lombok (Arrependimento) nossos testes necessitaram ficar redundante
* para chegar aos 70% de coberturas necessários, visto que, o @data, cria além dos getters e setter os toHash, toString e o toEqual
* que o JaCoCo reconhece e cobra
* */
class CursoModelTest {

    private CursoModel novoCurso() {
        return new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 100L, "Docker na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/docker");
    }

    @Nested
    class ConstrutoresEGettersSetters {

        @Test
        void deveCriarComConstrutorVazioESetters() {
            CursoModel curso = new CursoModel();
            curso.setId("64f1c2b2a1b2c3d4e5f6a7b8");
            curso.setCursoId(100L);
            curso.setNome("Docker na Pratica");
            curso.setInstituicao("DIO");
            curso.setCategoria("DevOps");
            curso.setCargaHoraria(30);
            curso.setLink("http://exemplo.com/docker");

            assertThat(curso.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
            assertThat(curso.getCursoId()).isEqualTo(100L);
            assertThat(curso.getNome()).isEqualTo("Docker na Pratica");
            assertThat(curso.getInstituicao()).isEqualTo("DIO");
            assertThat(curso.getCategoria()).isEqualTo("DevOps");
            assertThat(curso.getCargaHoraria()).isEqualTo(30);
            assertThat(curso.getLink()).isEqualTo("http://exemplo.com/docker");
        }

        @Test
        void deveCriarComConstrutorCompleto() {
            CursoModel curso = novoCurso();

            assertThat(curso.getId()).isEqualTo("64f1c2b2a1b2c3d4e5f6a7b8");
            assertThat(curso.getCursoId()).isEqualTo(100L);
            assertThat(curso.getNome()).isEqualTo("Docker na Pratica");
            assertThat(curso.getInstituicao()).isEqualTo("DIO");
            assertThat(curso.getCategoria()).isEqualTo("DevOps");
            assertThat(curso.getCargaHoraria()).isEqualTo(30);
            assertThat(curso.getLink()).isEqualTo("http://exemplo.com/docker");
        }
    }

    @Nested
    class Equals {

        @Test
        void deveSerIgualAoComparadoComEleMesmo() {
            CursoModel curso = novoCurso();

            assertThat(curso).isEqualTo(curso);
            assertThat(curso.equals(curso)).isTrue();
        }

        @Test
        void naoDeveSerIgualANulo() {
            CursoModel curso = novoCurso();

            assertThat(curso).isNotEqualTo(null);
            assertThat(curso.equals(null)).isFalse();
        }

        @Test
        void naoDeveSerIgualAObjetoDeOutraClasse() {
            CursoModel curso = novoCurso();

            assertThat(curso).isNotEqualTo("uma string qualquer");
            assertThat(curso.canEqual("uma string qualquer")).isFalse();
        }

        @Test
        void doisCursosComMesmosDadosDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = novoCurso();

            assertThat(c1).isEqualTo(c2);
            assertThat(c1.canEqual(c2)).isTrue();
        }

        @Test
        void cursosComIdsDiferentesNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c3b3a1b3c3d4e5f6a7b8", 100L, "Docker na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/docker");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComCursoIdDiferenteNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 200L, "Docker na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/docker");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComNomeDiferenteNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 100L, "Kubernetes na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/docker");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComInstituicaoDiferenteNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 100L, "Docker na Pratica", "Alura", "DevOps", 30, "http://exemplo.com/docker");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComCategoriaDiferenteNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 100L, "Docker na Pratica", "DIO", "Cloud", 30, "http://exemplo.com/docker");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComCargaHorariaDiferenteNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 100L, "Docker na Pratica", "DIO", "DevOps", 60, "http://exemplo.com/docker");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComLinkDiferenteNaoDevemSerIguais() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", 100L, "Docker na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/outro-link");

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        void cursosComTodosOsCamposNulosDevemSerIguais() {
            CursoModel c1 = new CursoModel(null, null, null, null, null, 0, null);
            CursoModel c2 = new CursoModel(null, null, null, null, null, 0, null);

            assertThat(c1).isEqualTo(c2);
        }

        @Test
        void cursoComIdNuloNaoDeveSerIgualAoComIdPreenchido() {
            CursoModel c1 = new CursoModel(null, 100L, "Docker na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/docker");
            CursoModel c2 = novoCurso();

            assertThat(c1).isNotEqualTo(c2);
            assertThat(c2).isNotEqualTo(c1);
        }

        @Test
        void cursoComCursoIdNuloNaoDeveSerIgualAoComCursoIdPreenchido() {
            CursoModel c1 = new CursoModel("64f1c2b2a1b2c3d4e5f6a7b8", null, "Docker na Pratica", "DIO", "DevOps", 30, "http://exemplo.com/docker");
            CursoModel c2 = novoCurso();

            assertThat(c1).isNotEqualTo(c2);
            assertThat(c2).isNotEqualTo(c1);
        }
    }

    @Nested
    class HashCode {

        @Test
        void doisCursosIguaisDevemTerOMesmoHashCode() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = novoCurso();

            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        void cursosDiferentesDevemTerHashCodesDiferentes() {
            CursoModel c1 = novoCurso();
            CursoModel c2 = new CursoModel("64f1c3b3a1b3c3d4e5f6a7b8", 200L, "Outro Curso", "Alura", "Cloud", 60, "http://outro.com");

            assertThat(c1.hashCode()).isNotEqualTo(c2.hashCode());
        }

        @Test
        void naoDeveLancarExcecaoAoCalcularHashCodeComCamposNulos() {
            CursoModel curso = new CursoModel(null, null, null, null, null, 0, null);

            assertThat(curso.hashCode()).isNotNull();
        }
    }

    @Nested
    class ToString {

        @Test
        void deveConterOsDadosDoCurso() {
            CursoModel curso = novoCurso();

            assertThat(curso.toString())
                    .isNotBlank()
                    .contains("Docker na Pratica")
                    .contains("DIO")
                    .contains("DevOps");
        }
    }
}