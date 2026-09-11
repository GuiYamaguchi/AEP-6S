package com.aep.cursoLivre.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveTratarResourceNotFoundException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new ResourceNotFoundException("curso nao encontrado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("curso nao encontrado");
        assertThat(response.getBody().getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Test
    void deveTratarBadRequestException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequest(new BadRequestException("dados invalidos"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("dados invalidos");
    }

    @Test
    void deveTratarIllegalArgumentException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgument(new IllegalArgumentException("argumento invalido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("argumento invalido");
    }

    @Test
    void deveTratarServiceException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleServiceException(new ServiceException("erro interno de servico"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("erro interno de servico");
    }

    @Test
    void deveTratarExcecaoGenerica() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGeneric(new RuntimeException("falha inesperada"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).contains("falha inesperada");
    }
}
