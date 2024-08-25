package com.fullcycle.admin.catalog.domain.castmeber;

import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.Validator;

public class CastMemberValidator extends Validator {
    private static final int NAME_MAX_LENGTH = 255;
    private static final int NAME_MIN_LENGTH = 3;
    private final CastMember castMember;

    public CastMemberValidator(final CastMember aMember, final ValidationHandler aHanlder) {
        super(aHanlder);
        this.castMember = aMember;
    }

    @Override
    public void validate() {
        checkNameConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.castMember.getName();
        if(name == null){
            this.validationHandler().append(new Error(("'name' should not be null")));
            return;
        }
        if(name.isBlank()){
            this.validationHandler().append(new Error("'name' should not be empty"));
        }
        final int length = name.trim().length();
        if(length > NAME_MAX_LENGTH || length < NAME_MIN_LENGTH){
            this.validationHandler().append(new Error("'name' must be between 3 and 255 characters"));
        }
    }

    private void checkTypeConstraints() {
        if(this.castMember.getType() == null) {
            this.validationHandler().append(new Error(("'type' should not be null")));
            return;
        }
    }
}
