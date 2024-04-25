package com.fullcycle.admin.catalog.domain.validation;

import java.util.List;

public interface ValidationHandler {
    // Interface fluent, poder chamar ela e ir encadeando
    ValidationHandler append(Error anError);

    ValidationHandler append(ValidationHandler anHandler);

    <T> T validate(Validation<T> aValidation);

    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && !(getErrors().size() == 0);
    }

    interface Validation<T> {
        T validate();
    }

    default Error firstError() {
        if (getErrors() == null || getErrors().isEmpty()) {
            return null;
        }
        return getErrors().get(0);
    }
}
