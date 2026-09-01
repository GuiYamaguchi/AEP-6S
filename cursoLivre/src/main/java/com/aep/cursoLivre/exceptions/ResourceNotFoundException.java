package com.aep.cursoLivre.exceptions;

/**
 * Exceção lançada quando um recurso solicitado (ex: Curso) não é encontrado
 * na base de dados. Deve resultar em uma resposta HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
