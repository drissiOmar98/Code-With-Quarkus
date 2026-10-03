package org.omar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Standard error payload returned by every failing request")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        @Schema(example = "2026-10-03T10:15:30Z") Instant timestamp,
        @Schema(example = "404") int status,
        @Schema(example = "Not Found") String error,
        @Schema(example = "No film found with id 9999") String message,
        @Schema(example = "/films/9999") String path,
        @Schema(description = "Per-field details, present for validation errors only")
        List<FieldError> details) {

    public record FieldError(
            @Schema(example = "minLength") String field,
            @Schema(example = "must be greater than or equal to 0") String message) {
    }

    public static ErrorResponse of(Response.StatusType status, String message,
                                   String path, List<FieldError> details) {
        return new ErrorResponse(Instant.now(), status.getStatusCode(),
                status.getReasonPhrase(), message, path, details);
    }
}