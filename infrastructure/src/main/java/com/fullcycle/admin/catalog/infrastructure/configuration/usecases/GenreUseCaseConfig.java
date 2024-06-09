package com.fullcycle.admin.catalog.infrastructure.configuration.usecases;

import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.create.DefaultCreateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.delete.DefaultDeleteGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.retrive.get.DefaultGetGenreByIdUseCase;
import com.fullcycle.admin.catalog.application.genre.retrive.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalog.application.genre.retrive.list.DefaultListGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.retrive.list.ListGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.update.DefaultUpdateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class GenreUseCaseConfig {
    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public GenreUseCaseConfig(final GenreGateway genreGateway, final CategoryGateway categoryGateway){
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase(){
        return new DefaultCreateGenreUseCase(categoryGateway, genreGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase(){
        return new DefaultDeleteGenreUseCase(genreGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase(){
        return new DefaultGetGenreByIdUseCase(genreGateway);
    }

    @Bean
    public ListGenreUseCase listGenreUseCase(){
        return new DefaultListGenreUseCase(genreGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreUseCase(){
        return new DefaultUpdateGenreUseCase(categoryGateway, genreGateway);
    }
}
