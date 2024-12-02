package cleverton.heusner.adapter.input.exception.mapper;

import cleverton.heusner.domain.exception.ResourceNotFoundException;
import cleverton.heusner.adapter.input.exception.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(final ResourceNotFoundException resourceNotFoundException) {
        return Response.status(NOT_FOUND)
                .entity(new ErrorResponse(
                        "Resource Not Found",
                        resourceNotFoundException.getMessage(),
                        NOT_FOUND.getStatusCode())
                )
                .build();
    }
}