package com.fullcycle.admin.catalog.domain.castmeber;

import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.Validator;

public class CastMamberValidator extends Validator {

    private final CastMember castMember;

    public CastMamberValidator(final CastMember aMember, final ValidationHandler aHanlder) {
        super(aHanlder);
        this.castMember = aMember;
    }

    @Override
    public void validate() {

    }
}
