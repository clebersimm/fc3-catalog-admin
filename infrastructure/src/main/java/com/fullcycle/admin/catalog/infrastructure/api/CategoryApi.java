package com.fullcycle.admin.catalog.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryListResponse;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryApiInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(value = "categories")
@Tag(name = "Categories")
public interface CategoryApi {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfuly"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error thrown")
    })
    ResponseEntity<?> createCategory(@RequestBody CreateCategoryApiInput input);

    @GetMapping
    @Operation(summary = "List all categories paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed successfully"),
            @ApiResponse(responseCode = "422", description = "An invalid parameter was received"),
            @ApiResponse(responseCode = "500", description = "An internal server error thrown")
    })
    Pagination<CategoryListResponse> listCategories(
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "name") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "10") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction);

    @GetMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a category by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrived"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error thrown")
    })
    CategoryApiOutput getById(@PathVariable(name = "id") String id);

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a category by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error thrown")
    })
    ResponseEntity<?> updateById(@PathVariable(name = "id") String id, @RequestBody UpdateCategoryApiInput input);

    @DeleteMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error thrown")
    })
    void deleteById(@PathVariable(name = "id") String id);
}
