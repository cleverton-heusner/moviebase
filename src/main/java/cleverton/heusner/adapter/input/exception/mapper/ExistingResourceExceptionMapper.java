package cleverton.heusner.adapter.input.exception.mapper;

import cleverton.heusner.adapter.input.exception.ErrorResponse;
import cleverton.heusner.domain.exception.ExistingResourceException;
import cleverton.heusner.domain.exception.ResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public class ExistingResourceExceptionMapper implements ExceptionMapper<ExistingResourceException> {

    @Override
    public Response toResponse(final ExistingResourceException existingResourceException) {
        return Response.status(CONFLICT)
                .entity(new ErrorResponse(
                        "Existing Resource",
                        existingResourceException.getMessage(),
                        CONFLICT.getStatusCode())
                )
                .build();
    }
}