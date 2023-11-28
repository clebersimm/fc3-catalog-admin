package com.fullcycle.admin.catalog.domain.exceptions;

public class NoStackException extends RuntimeException {
    public NoStackException(final String message){
        this(message, null);
    }
    public NoStackException(final String message, final Throwable cause){
        //Remove a stack do erro, desta forma a JVM não precisa dar um stop na thread para
        //buscar as informações da stack inteira
        super(message, cause, true, false);
    }
}
