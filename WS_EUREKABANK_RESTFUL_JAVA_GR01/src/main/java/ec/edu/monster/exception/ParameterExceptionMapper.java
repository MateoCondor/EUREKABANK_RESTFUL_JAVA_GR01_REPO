package ec.edu.monster.exception;

import ec.edu.monster.dto.ErrorResponseDTO;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ParameterExceptionMapper implements ExceptionMapper<ParameterException> {

    @Override
    public Response toResponse(ParameterException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
