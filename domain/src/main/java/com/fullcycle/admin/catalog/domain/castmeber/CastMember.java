package com.fullcycle.admin.catalog.domain.castmeber;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.time.Instant;

public class CastMember extends AggregateRoot<CastMemberId> {
    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    protected CastMember(
            final CastMemberId anId,
            final String aName,
            final CastMemberType aType,
            final Instant aCreationDate,
            final Instant anUpdateDate
    ){
        super(anId);
        this.name = aName;
        this.type = aType;
        this.createdAt = aCreationDate;
        this.updatedAt = anUpdateDate;
        selfValidate();
    }

    public static CastMember newMember(final String aName, final CastMemberType aType) {
        final var anId = CastMemberId.unique();
        final var now = InstantUtils.now();
        return new CastMember(anId, aName, aType, now, now);
    }

    public static CastMember with(
            final CastMemberId anId,
            final String aName,
            final CastMemberType aType,
            final Instant aCreationDate,
            final Instant anUpdateDate
    ) {
        return new CastMember(anId, aName, aType, aCreationDate, anUpdateDate);
    }

    public static CastMember with(
            final CastMember aCastMember
    ) {
        return new CastMember(aCastMember.id, aCastMember.name, aCastMember.type, aCastMember.createdAt, aCastMember.updatedAt);
    }

    public CastMember update(final String aName, final CastMemberType aType) {
        this.name = aName;
        this.type = aType;
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    public String getName() {
        return name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void validate(ValidationHandler aHandler) {
        new CastMemberValidator(this, aHandler).validate();
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);
        if(notification.hasError()){
            throw new NotificationException("Failed to create a Aggregate CastMember", notification);
        }
    }

}
