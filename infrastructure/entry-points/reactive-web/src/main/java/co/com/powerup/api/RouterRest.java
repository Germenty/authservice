package co.com.powerup.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routes(Handler handler) {
        return RouterFunctions.route()
                .GET("/api/v1/users", handler::getAllUsersUseCase) //  todos
                .GET("/api/v1/user", handler::getUserByEmailUseCase) //  email
                .POST("/api/v1/users", handler::createUserUseCase) // Crear usuario
                .build();
    }
}
