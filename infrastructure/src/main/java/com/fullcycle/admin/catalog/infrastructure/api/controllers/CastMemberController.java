package com.fullcycle.admin.catalog.infrastructure.api.controllers;

import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberCommand;
import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalog.infrastructure.api.CastMemberAPI;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;

    public CastMemberController(final CreateCastMemberUseCase createCastMemberUseCase) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
    }

    @Override
    public ResponseEntity<?> create(CreateCastMemberRequest input) {
        final var output = this.createCastMemberUseCase.execute(CreateCastMemberCommand.with(input.name(), input.type()));

        return ResponseEntity.created(URI.create("/cast_members/"+output.id())).body(output);
    }
}
