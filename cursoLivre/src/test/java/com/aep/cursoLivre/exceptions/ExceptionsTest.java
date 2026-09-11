package com.aep.cursoLivre.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void badRequestExceptionDeveGuardarMensagem() {
        BadRequestException ex = new BadRequestException("mensagem de erro");
        assertThat(ex.getMessage()).isEqualTo("mensagem de erro");
    }

    @Test
    void badRequestExceptionDeveGuardarMensagemECausa() {
        Throwable causa = new RuntimeException("causa raiz");
        BadRequestException ex = new BadRequestException("mensagem de erro", causa);

        assertThat(ex.getMessage()).isEqualTo("mensagem de erro");
        assertThat(ex.getCause()).isEqualTo(causa);
    }

    @Test
    void resourceNotFoundExceptionDeveGuardarMensagem() {
        ResourceNotFoundException ex = new ResourceNotFoundException("nao encontrado");
        assertThat(ex.getMessage()).isEqualTo("nao encontrado");
    }

    @Test
    void resourceNotFoundExceptionDeveGuardarMensagemECausa() {
        Throwable causa = new RuntimeException("causa raiz");
        ResourceNotFoundException ex = new ResourceNotFoundException("nao encontrado", causa);

        assertThat(ex.getMessage()).isEqualTo("nao encontrado");
        assertThat(ex.getCause()).isEqualTo(causa);
    }

    @Test
    void serviceExceptionDeveGuardarMensagem() {
        ServiceException ex = new ServiceException("erro de servico");
        assertThat(ex.getMessage()).isEqualTo("erro de servico");
    }

    @Test
    void serviceExceptionDeveGuardarMensagemECausa() {
        Throwable causa = new RuntimeException("causa raiz");
        ServiceException ex = new ServiceException("erro de servico", causa);

        assertThat(ex.getMessage()).isEqualTo("erro de servico");
        assertThat(ex.getCause()).isEqualTo(causa);
    }

    @Test
    void errorResponseDeveExporTodosOsCampos() {
        ErrorResponse response = new ErrorResponse(404, "Not Found", "Curso nao encontrado");

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getMessage()).isEqualTo("Curso nao encontrado");
        assertThat(response.getTimestamp()).isNotBlank();
    }
}
