package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.util.Objects;

public final class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(final CreateCastMemberCommand aCommand) {
        final var notification = Notification.create();
        final var aMember = notification.validate(() -> CastMember.newMember(aCommand.name(), aCommand.type()));
        if(notification.hasError()){
            notify(notification);
        }
        return CreateCastMemberOutput.from(castMemberGateway.create(aMember));
    }

    private static void notify(Notification notification) {
        throw new NotificationException("Could not create Aggregate CastMember", notification);
    }
}
