package com.aep.cursoLivre.models;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

final class ModelContract {

    private ModelContract() {
    }

    static <T> void verificarEqualsEHashCode(
            Supplier<T> fabrica,
            List<Consumer<T>> alteracoes,
            List<Consumer<T>> anulacoes) {

        T base = fabrica.get();
        T copia = fabrica.get();

        assertThat(base.equals(base)).isTrue();
        assertThat(base.equals(copia)).isTrue();
        assertThat(copia.equals(base)).isTrue();
        assertThat(base.hashCode()).isEqualTo(copia.hashCode());
        assertThat(base.equals(null)).isFalse();
        assertThat(base.equals("objeto de outro tipo")).isFalse();

        for (Consumer<T> alteracao : alteracoes) {
            T variante = fabrica.get();
            alteracao.accept(variante);

            assertThat(base.equals(variante)).isFalse();
            assertThat(variante.equals(base)).isFalse();
        }

        for (Consumer<T> anulacao : anulacoes) {
            T a = fabrica.get();
            anulacao.accept(a);
            T b = fabrica.get();
            anulacao.accept(b);

            assertThat(base.equals(a)).isFalse();
            assertThat(a.equals(base)).isFalse();
            assertThat(a.equals(b)).isTrue();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }
    }
}
