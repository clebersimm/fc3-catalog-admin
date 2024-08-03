package com.fullcycle.admin.catalog.e2e;

import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.function.Function;

public interface MockDsl {
    MockMvc mvc();

    default CategoryID givenACategory(final String expectedName, final String expectedDescription,
                                      final boolean expectedIsActive) throws Exception {
        final var request = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
        final var actualId = this.given("/categories", request);
        return CategoryID.from(actualId);
    }

    default GenreID givenAGenre(final String expectedName, final boolean expectedIsActive, final List<CategoryID> categories) throws Exception {
        final var request = new CreateGenreRequest(expectedName, mapTo(categories,CategoryID::getValue), expectedIsActive);
        final var actualId = this.given("/genres", request);
        return GenreID.from(actualId);
    }

    private String given(final String url, final Object body) throws Exception {
        final var aRequest = MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));
        final var actualId = this.mvc().perform(aRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");
        return actualId;
    }

    /*
        Helper
        A = actual
        D = destination
     */
    default <A,D> List<D> mapTo(final List<A> actual, final Function<A,D> mapper){
        return actual.stream().map(mapper).toList();
    }
}
