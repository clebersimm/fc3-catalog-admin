package com.fullcycle.admin.catalog.infrastructure.api.controllers;

import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberCommand;
import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.delete.DeleteCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberCommand;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.api.CastMemberAPI;
import com.fullcycle.admin.catalog.infrastructure.castmember.CastMemberPresenter;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;
    private final ListCastMemberUseCase listCastMemberUseCase;

    public CastMemberController(final CreateCastMemberUseCase createCastMemberUseCase, final GetCastMemberByIdUseCase getCastMemberByIdUseCase, final UpdateCastMemberUseCase updateCastMemberUseCase, final DeleteCastMemberUseCase deleteCastMemberUseCase, final ListCastMemberUseCase listCastMemberUseCase) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
        this.listCastMemberUseCase = Objects.requireNonNull(listCastMemberUseCase);
    }

    @Override
    public ResponseEntity<?> create(CreateCastMemberRequest input) {
        final var output = this.createCastMemberUseCase.execute(CreateCastMemberCommand.with(input.name(), input.type()));

        return ResponseEntity.created(URI.create("/cast_members/"+output.id())).body(output);
    }

    @Override
    public Pagination<CastMemberListResponse> list(final String search,final int page,final int perPage,final String sort,final String direction) {
        return this.listCastMemberUseCase.execute(new SearchQuery(page, perPage, search, sort, direction)).map(CastMemberPresenter::present);
    }

    @Override
    public CastMemberResponse getById(String id) {
        return CastMemberPresenter.present(this.getCastMemberByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(String id, UpdateCastMemberRequest aBody) {
        final var output = this.updateCastMemberUseCase.execute(UpdateCastMemberCommand.with(id, aBody.name(), aBody.type()));
        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(String id) {
        this.deleteCastMemberUseCase.execute(id);
    }
}
