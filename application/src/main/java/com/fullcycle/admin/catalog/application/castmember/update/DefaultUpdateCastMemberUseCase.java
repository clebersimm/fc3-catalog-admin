package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public final class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(UpdateCastMemberCommand aCommand) {
        final var anId = CastMemberId.from(aCommand.id());

        final var aMember = this.castMemberGateway.findById(anId).orElseThrow(notFound(anId));
        final var notification = Notification.create();
        notification.validate(() -> aMember.update(aCommand.name(), aCommand.type()));
        if(notification.hasError()){
            notify(notification, anId);
        }
        return UpdateCastMemberOutput.from(this.castMemberGateway.update(aMember));
    }

    private void notify(final Notification notification, final Identifier anId){
        throw new NotificationException("Could not update Aggregate CastMember %s".formatted(anId), notification);
    }

    private Supplier<NotFoundException> notFound(final CastMemberId anId) {
        return () -> NotFoundException.with(CastMember.class, anId);
    }

}
