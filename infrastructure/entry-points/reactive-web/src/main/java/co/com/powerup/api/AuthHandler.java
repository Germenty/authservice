package co.com.powerup.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.model.jwt.UserCredential;
import co.com.powerup.usecase.auth.AuthUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthUseCase authUseCase;

    // POST /api/v1/login
    public Mono<ServerResponse> loginAuthUseCase(ServerRequest request) {
        return request
                .bodyToMono(UserCredential.class)
                .flatMap(authUseCase::login)
                .flatMap(token -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(token));
    }
}
