package com.incaas.tjrn.exception.exceptions;

public class ProcessoJudicialComNumeroJaExisteException extends RuntimeException {
    public ProcessoJudicialComNumeroJaExisteException(String message) {
        super("Já existe um processo com este número: " + message);
    }
}
