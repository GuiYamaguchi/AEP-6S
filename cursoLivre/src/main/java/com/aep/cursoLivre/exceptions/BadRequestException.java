package com.aep.cursoLivre.exceptions;

/**
 * Exceção lançada quando os dados enviados na requisição são inválidos
 * (nulos, vazios, mal formatados, etc). Deve resultar em uma resposta
 * HTTP 400 (Bad Request).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
