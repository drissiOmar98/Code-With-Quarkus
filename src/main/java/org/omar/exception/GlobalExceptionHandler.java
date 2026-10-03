package org.omar.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.omar.dto.ErrorResponse;

import java.util.List;

public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    /** Business error: unknown film id -> 404. */
    @ServerExceptionMapper
    public Response handleFilmNotFound(FilmNotFoundException ex, UriInfo uriInfo) {
        return build(Response.Status.NOT_FOUND, ex.getMessage(), uriInfo, null);
    }

    /** Invalid input (@Min, @Positive, ...) -> 400 with the list of offending fields. */
    @ServerExceptionMapper
    public Response handleValidation(ConstraintViolationException ex, UriInfo uriInfo) {
        List<ErrorResponse.FieldError> details = ex.getConstraintViolations().stream()
                .map(v -> new ErrorResponse.FieldError(fieldName(v), v.getMessage()))
                .toList();
        return build(Response.Status.BAD_REQUEST, "Validation failed", uriInfo, details);
    }

    /** Framework errors (unknown URL, wrong HTTP method, bad parameter format...) keep their status. */
    @ServerExceptionMapper
    public Response handleWebApplication(WebApplicationException ex, UriInfo uriInfo) {
        Response.StatusType status = ex.getResponse().getStatusInfo();
        if (status.getStatusCode() >= 500) {
            LOG.errorf(ex, "Server error on %s", path(uriInfo));
        }
        return build(status, ex.getMessage(), uriInfo, null);
    }

    /** Safety net: log the real cause, never leak internals to the client. */
    @ServerExceptionMapper
    public Response handleUnexpected(Exception ex, UriInfo uriInfo) {
        LOG.errorf(ex, "Unhandled exception on %s", path(uriInfo));
        return build(Response.Status.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", uriInfo, null);
    }

    private Response build(Response.StatusType status, String message,
                           UriInfo uriInfo, List<ErrorResponse.FieldError> details) {
        ErrorResponse body = ErrorResponse.of(status, message, path(uriInfo), details);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    private static String path(UriInfo uriInfo) {
        return uriInfo.getRequestUri().getPath();
    }

    /** "updateRentalRate.minLength" -> "minLength" */
    private static String fieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }
}