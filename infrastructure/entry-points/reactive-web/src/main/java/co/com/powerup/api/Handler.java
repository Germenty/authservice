package co.com.powerup.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.model.user.User;
import co.com.powerup.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    // GET /api/v1/users?email=xxx
    public Mono<ServerResponse> listenGETUseCase(ServerRequest request) {
        String email = request.queryParam("email").orElse("");
        return userUseCase.findUserByEmail(email)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // GET /api/v1/users
    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.findAllUsers(), User.class);
    }

    // POST /api/v1/users
    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(userUseCase::saveUser)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }
}
