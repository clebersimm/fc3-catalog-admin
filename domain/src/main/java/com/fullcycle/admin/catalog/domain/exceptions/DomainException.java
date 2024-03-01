package com.fullcycle.admin.catalog.domain.exceptions;

import com.fullcycle.admin.catalog.domain.validation.Error;

import java.util.List;

public class DomainException extends NoStackException {
    protected final List<Error> errors;
    protected DomainException(final String aMessage, final List<Error> anErrors){
        super(aMessage);
        this.errors = anErrors;
    }
    public static DomainException with(final Error anError){
        return new DomainException(anError.message(),List.of(anError));
    }
    public static DomainException with(final List<Error> anError){
        return new DomainException("",anError);
    }
    public List<Error> getErrors() {
        return errors;
    }
}
