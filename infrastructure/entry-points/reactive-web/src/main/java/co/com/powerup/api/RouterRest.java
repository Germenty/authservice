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
                .GET("/api/v1/users", handler::listenGETOtherUseCase) //  todos
                .GET("/api/v1/user", handler::listenGETUseCase) //  email
                .POST("/api/v1/users", handler::listenPOSTUseCase) // Crear usuario
                .build();
    }
}
