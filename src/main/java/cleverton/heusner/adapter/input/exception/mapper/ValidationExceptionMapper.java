package cleverton.heusner.adapter.input.exception.mapper;

import cleverton.heusner.adapter.input.exception.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final String PROPERTY_MESSAGE_SEPARATOR = ": ";
    private final static byte MAX_VIOLATIONS_BY_MESSAGE = 1;

    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        return Response.status(BAD_REQUEST)
                .entity(new ErrorResponse(
                        "Bad Request",
                        getViolationMessage(exception),
                        BAD_REQUEST.getStatusCode())
                )
                .build();
    }

    private String getViolationMessage(final ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + PROPERTY_MESSAGE_SEPARATOR + violation.getMessage())
                .limit(MAX_VIOLATIONS_BY_MESSAGE)
                .collect(Collectors.joining());
    }
}