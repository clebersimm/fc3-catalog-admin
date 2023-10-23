package com.fullcycle.admin.catalog.domain.validation;

import java.util.List;

public interface ValidationHandler {
    //Interface fluent, poder chamar ela e ir encadeando
    ValidationHandler append(Error anError);
    ValidationHandler append(ValidationHandler anHandler);
    ValidationHandler validate(Validation aValidation);
    List<Error> getErrors();
    default boolean hasError(){
        return getErrors() != null && !(getErrors().size() == 0);
    }
    interface Validation {
        void validate();
    }
}
