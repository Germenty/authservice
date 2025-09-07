package co.com.powerup.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/users", produces = {
                    "application/json" }, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "getUserByEmailUseCase", operation = @Operation(operationId = "getUserByEmail", summary = "Buscar usuario por email", parameters = {
                            @Parameter(name = "email", description = "Email del usuario", required = true)
                    }, responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = User.class))),
                            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                            @ApiResponse(responseCode = "400", description = "Parámetro inválido"),
                            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                    })),
            @RouterOperation(path = "/api/v1/users", produces = { "application/json" }, consumes = {
                    "application/json" }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "createUserUseCase", operation = @Operation(operationId = "createUser", summary = "Crear un nuevo usuario", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = User.class))), responses = {
                            @ApiResponse(responseCode = "201", description = "Usuario creado", content = @Content(schema = @Schema(implementation = User.class))),
                            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                    }))
    })
    public RouterFunction<ServerResponse> routes(Handler handler) {
        return RouterFunctions.route()
                .GET("/api/v1/users", handler::getUserByEmailUseCase) // buscar usuario por email
                .POST("/api/v1/users", handler::createUserUseCase) // crear usuario
                .build();
    }

}
