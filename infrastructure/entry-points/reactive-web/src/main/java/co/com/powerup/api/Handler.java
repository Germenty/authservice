package co.com.powerup.api;

import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.api.dto.RegisterUserRequest;
import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.user.User;
import co.com.powerup.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    // GET /api/v1/users?email=xxx
    public Mono<ServerResponse> getUserByEmailUseCase(ServerRequest request) {
        String email = request.queryParam("email").orElse("");
        return userUseCase.getUserByEmail(email)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    // POST /api/v1/users
    public Mono<ServerResponse> createUserUseCase(ServerRequest request) {
        return request
                .bodyToMono(RegisterUserRequest.class)

                // 1. DTO → Dominio (usar dto.firstName() en lugar de getFirstName())
                .map(dto -> User.builder()
                        .name(dto.firstName())
                        .lastName(dto.lastName())
                        .birthDate(dto.birthDate())
                        .address(dto.address())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .baseSalary(dto.baseSalary())
                        .rol(Rol.builder()
                                .id(dto.roleId())
                                .build())
                        .build())

                // 2. Invocar caso de uso
                .flatMap(userUseCase::createUser)

                // 3. Responder 201 Created
                .flatMap(saved -> ServerResponse.created(
                        URI.create("/api/v1/users?email=" + saved.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved))

                // 4. Manejo de errores de negocio
                .onErrorResume(IllegalArgumentException.class, ex -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", ex.getMessage())));
    }
}