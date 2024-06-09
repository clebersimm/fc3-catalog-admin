package com.fullcycle.admin.catalog.infrastructure.genre;

import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre create(Genre aGenre) {
        return save(aGenre);
    }

    private Genre save(Genre aGenre) {
        return this.genreRepository.save(GenreJpaEntity.from(aGenre)).toAggregate();
    }

    @Override
    public void deleteById(final GenreID anId) {
        final var aGenreId = anId.getValue();
        if (this.genreRepository.existsById(aGenreId)) {
            this.genreRepository.deleteById(aGenreId);
        }
    }

    @Override
    public Optional<Genre> findById(final GenreID anID) {
        return this.genreRepository.findById(anID.getValue()).map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()),aQuery.sort())
        );
        final var where = Optional.ofNullable(aQuery.terms()).filter(str -> !str.isBlank()).map(this::assembleSpecification).orElse(null);
        final var results = this.genreRepository.findAll(Specification.where(where),page);
        return new Pagination<>(results.getNumber(), results.getSize(), results.getTotalElements(), results.map(GenreJpaEntity::toAggregate).toList());
    }

    private Specification<GenreJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }
}
