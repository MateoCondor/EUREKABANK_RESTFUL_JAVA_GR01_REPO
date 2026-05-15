package ec.edu.monster.exception;

import ec.edu.monster.dto.ErrorResponseDTO;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AccountExceptionMapper implements ExceptionMapper<AccountException> {

    @Override
    public Response toResponse(AccountException exception) {
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getMessage());
        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
