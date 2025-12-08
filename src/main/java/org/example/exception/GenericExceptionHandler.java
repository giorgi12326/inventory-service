package org.example.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(new ErrorResponse("Internal server error", exception.getMessage()))
                       .build();
    }

    public static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
