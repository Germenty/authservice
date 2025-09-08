package co.com.powerup.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class AuthRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/login", produces = {
                    "application/json" }, method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "loginAuthUseCase", operation = @Operation(operationId = "loginAuth", summary = "Autenticación de usuario", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Credenciales de acceso (email y password)"), responses = {
                            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(example = "{ \"token\": \"jwt_token_here\" }"))),
                            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
                            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                    }))
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/login", handler::loginAuthUseCase) // login
                .build();
    }
}
