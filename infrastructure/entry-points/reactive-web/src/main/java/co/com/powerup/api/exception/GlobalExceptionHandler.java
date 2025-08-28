package co.com.powerup.api.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        HttpStatus status;
        String error;

        if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            error = "Bad Request";
        } else if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            error = "Not Found";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                error,
                ex.getMessage(),
                status.value(),
                exchange.getRequest().getPath().value(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes)));
        } catch (Exception e) {
            log.error("Error writing error response", e);
            return Mono.error(e);
        }
    }
}
