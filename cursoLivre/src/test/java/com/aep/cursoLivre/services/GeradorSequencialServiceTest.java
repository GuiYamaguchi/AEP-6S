package com.aep.cursoLivre.services;

import com.aep.cursoLivre.models.DatabaseSequenceModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeradorSequencialServiceTest {

    @Mock
    private MongoOperations mongoOperations;

    private GeradorSequencialService geradorSequencialService;

    @BeforeEach
    void setUp() {
        geradorSequencialService = new GeradorSequencialService(mongoOperations);
    }

    @Test
    void deveRetornarProximoValorDaSequencia() {
        DatabaseSequenceModel counter = new DatabaseSequenceModel();
        counter.setId("curso_sequence");
        counter.setSeq(5L);

        when(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(DatabaseSequenceModel.class)))
                .thenReturn(counter);

        long resultado = geradorSequencialService.generateSequence("curso_sequence");

        assertThat(resultado).isEqualTo(5L);
    }

    @Test
    void deveRetornarUmQuandoContadorForNulo() {
        when(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(DatabaseSequenceModel.class)))
                .thenReturn(null);

        long resultado = geradorSequencialService.generateSequence("curso_sequence");

        assertThat(resultado).isEqualTo(1L);
    }
}
