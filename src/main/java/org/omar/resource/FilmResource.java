package org.omar.resource;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.omar.dto.ErrorResponse;
import org.omar.dto.FilmDto;
import org.omar.dto.FilmWithActorsDto;
import org.omar.service.FilmService;

import java.math.BigDecimal;
import java.util.List;

@Path("/films")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Films")
public class FilmResource {

    private final FilmService filmService;

    public FilmResource(FilmService filmService) {
        this.filmService = filmService;
    }

    @GET
    @Path("/{filmId}")
    @Operation(summary = "Get a film by id")
    @APIResponse(responseCode = "200", description = "Film found")
    @APIResponse(responseCode = "400", description = "Invalid film id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Film not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public FilmDto getFilm(
            @Parameter(description = "Film identifier", example = "1")
            @PathParam("filmId") @Positive short filmId) {
        return filmService.getFilm(filmId);
    }

    @GET
    @Operation(summary = "List films longer than a given length (20 per page, sorted by length)")
    @APIResponse(responseCode = "200", description = "Page of films")
    @APIResponse(responseCode = "400", description = "Invalid page or minLength",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<FilmDto> getFilms(
            @Parameter(description = "Zero-based page number")
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @Parameter(description = "Minimum film length in minutes")
            @QueryParam("minLength") @DefaultValue("0") @Min(0) short minLength) {
        return filmService.getFilms(page, minLength);
    }

    @GET
    @Path("/with-actors")
    @Operation(summary = "Search films by title prefix and include their cast")
    @APIResponse(responseCode = "200", description = "Matching films with their actors")
    @APIResponse(responseCode = "400", description = "Invalid minLength",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<FilmWithActorsDto> getFilmsWithActors(
            @Parameter(description = "Title must start with this text", example = "A")
            @QueryParam("titlePrefix") @DefaultValue("") String titlePrefix,
            @Parameter(description = "Minimum film length in minutes")
            @QueryParam("minLength") @DefaultValue("0") @Min(0) short minLength) {
        return filmService.getFilmsWithActors(titlePrefix, minLength);
    }

    @PUT
    @Path("/rental-rate")
    @Operation(summary = "Update the rental rate of all films longer than a given length")
    @APIResponse(responseCode = "200", description = "The updated films")
    @APIResponse(responseCode = "400", description = "Invalid minLength or rentalRate",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<FilmDto> updateRentalRate(
            @Parameter(description = "Films longer than this (minutes) are updated", required = true)
            @QueryParam("minLength") @NotNull @Min(0) Short minLength,
            @Parameter(description = "New rental rate (max 2 integer and 2 decimal digits)",
                    example = "2.99", required = true)
            @QueryParam("rentalRate") @NotNull
            @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 2, fraction = 2)
            BigDecimal rentalRate) {
        return filmService.updateRentalRate(minLength, rentalRate);
    }
}