package cleverton.heusner.adapter.input.exception.mapper;

import cleverton.heusner.adapter.input.exception.ErrorResponse;
import cleverton.heusner.domain.exception.BusinessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(final BusinessException businessException) {
        return Response.status(UNPROCESSABLE_ENTITY.code())
                .entity(new ErrorResponse(
                        "Business Exception",
                        businessException.getMessage(),
                        UNPROCESSABLE_ENTITY.code())
                )
                .build();
    }
}