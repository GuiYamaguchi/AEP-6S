package com.aep.cursoLivre.exceptions;

/**
 * Exceção lançada quando ocorre uma falha inesperada durante uma operação
 * de negócio ou de persistência (ex: erro de acesso ao banco de dados).
 * Deve resultar em uma resposta HTTP 500 (Internal Server Error).
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
