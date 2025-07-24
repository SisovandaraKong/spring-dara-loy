package istad.co.chatrealtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class HandleServiceException {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> handleServiceException(ResponseStatusException exception) {
        return new ResponseEntity<>(Map.of(
                "message", "Error business logic",
                "status", exception.getStatusCode().value(),
                "details", exception.getReason()
        ), HttpStatus.BAD_REQUEST);
    }
}
