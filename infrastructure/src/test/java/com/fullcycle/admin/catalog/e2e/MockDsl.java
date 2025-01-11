package com.fullcycle.admin.catalog.e2e;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infrastructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.function.Function;

public interface MockDsl {
    MockMvc mvc();

    //----CastMember

    default ResultActions deleteCastMember(final CastMemberId anId ) throws Exception {
        return this.delete("/cast_members/",anId);
    }

    default CastMemberId givenACastMember(final String aName, final CastMemberType aType) throws Exception {
        final var request = new CreateCastMemberRequest(aName, aType);
        final var actualId = this.given("/cast_members", request);
        return CastMemberId.from(actualId);
    }

    default ResultActions givenACastMemberResult(final String aName, final CastMemberType aType) throws Exception {
        final var request = new CreateCastMemberRequest(aName, aType);
        return this.givenResult("/cast_members", request);
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search) throws Exception {
        return listCastMembers(page, perPage, search, "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage) throws Exception {
        return listCastMembers(page, perPage, "", "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search, final String sort,
                                         final String directions) throws Exception {
        return this.list("/cast_members", page, perPage, search, sort, directions);
    }

    default CastMemberResponse retrieveACastMember(final CastMemberId anID) throws Exception {
        return this.retrive("/cast_members/", anID, CastMemberResponse.class);
    }

    default ResultActions retrieveACastMemberResult(final CastMemberId anID) throws Exception {
        return this.retriveResult("/cast_members/", anID);
    }

    default ResultActions updateACastMember(final CastMemberId anID, final String aName, final CastMemberType aType) throws Exception {
        return this.put("/cast_members/", anID, new UpdateCastMemberRequest(aName, aType));
    }

    //----Category

    default ResultActions deleteACategory(final CategoryID anId ) throws Exception {
        return this.delete("/categories/",anId);
    }

    default CategoryID givenACategory(final String expectedName, final String expectedDescription,
                                      final boolean expectedIsActive) throws Exception {
        final var request = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
        final var actualId = this.given("/categories", request);
        return CategoryID.from(actualId);
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort,
                                         final String directions) throws Exception {
        return this.list("/categories", page, perPage, search, sort, directions);
    }

    default CategoryApiOutput retrieveACategory(final CategoryID anID) throws Exception {
        return this.retrive("/categories/", anID, CategoryApiOutput.class);
    }

    default ResultActions updateACategory(final CategoryID anID, final UpdateCategoryApiInput aUpdate) throws Exception {
        return this.put("/categories/", anID, aUpdate);
    }

    //----Genre

    default ResultActions deleteAGenre(final GenreID anId ) throws Exception {
        return this.delete("/genres/",anId);
    }

    default GenreID givenAGenre(final String expectedName, final boolean expectedIsActive, final List<CategoryID> categories) throws Exception {
        final var request = new CreateGenreRequest(expectedName, mapTo(categories,CategoryID::getValue), expectedIsActive);
        final var actualId = this.given("/genres", request);
        return GenreID.from(actualId);
    }

    default ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(final int page, final int perPage) throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search, final String sort,
                                         final String directions) throws Exception {
        return this.list("/genres", page, perPage, search, sort, directions);
    }

    default GenreResponse retrieveAGenre(final GenreID anID) throws Exception {
        return this.retrive("/genres/", anID, GenreResponse.class);
    }

    default ResultActions updateAGenre(final GenreID anID, final UpdateGenreRequest aUpdate) throws Exception {
        return this.put("/genres/", anID, aUpdate);
    }

    //-----------------------------Helpers------------------------------------

    private ResultActions delete(final String url, final Identifier anID) throws Exception {
        final var aRequest = MockMvcRequestBuilders.delete(url+anID.getValue()).contentType(MediaType.APPLICATION_JSON);
        return this.mvc().perform(aRequest);
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

    private ResultActions givenResult(final String url, final Object body) throws Exception {
        final var aRequest = MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));
        return this.mvc().perform(aRequest);
    }

    private ResultActions list(final String url,final int page, final int perPage, final String search, final String sort,
                                         final String directions) throws Exception {
        final var aRequest = MockMvcRequestBuilders.get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", directions)
                .contentType(MediaType.APPLICATION_JSON);
        return this.mvc().perform(aRequest);
    }

    /*
        Helper
        A = actual
        D = destination
    */
    default <A,D> List<D> mapTo(final List<A> actual, final Function<A,D> mapper){
        return actual.stream().map(mapper).toList();
    }

    private <T> T retrive(final String url, final Identifier anID, final Class<T> clazz) throws Exception {
        final var aRequest = MockMvcRequestBuilders.get(url + anID.getValue()).contentType(MediaType.APPLICATION_JSON);
        final var json = this.mvc().perform(aRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();
        return Json.readValue(json, clazz);
    }

    private ResultActions retriveResult(final String url, final Identifier anID) throws Exception {
        final var aRequest = MockMvcRequestBuilders.get(url + anID.getValue()).contentType(MediaType.APPLICATION_JSON);
        return this.mvc().perform(aRequest);
    }

    private ResultActions put(final String url, final Identifier anID, final Object requestBody) throws Exception {
        final var aRequest = MockMvcRequestBuilders.put(url+anID.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));
        return this.mvc().perform(aRequest);
    }
}
