package com.fullcycle.admin.catalog.application.genre.retrive.get;

import java.util.Objects;

import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {

    private final GenreGateway genreGateway;

    public DefaultGetGenreByIdUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public GenreOutput execute(final String anId) {
        final var aGenreId = GenreID.from(anId);
        return this.genreGateway.findById(aGenreId).map(GenreOutput::from)
                .orElseThrow(() -> NotFoundException.with(Genre.class, aGenreId));
    }

}
