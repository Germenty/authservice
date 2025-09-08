package co.com.powerup.usecase.auth;

import co.com.powerup.model.jwt.AccessToken;
import co.com.powerup.model.jwt.UserCredential;
import co.com.powerup.model.jwt.gateways.JWTRepository;
import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.rol.gateways.RolRepository;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final JWTRepository jwtRepository;

    public Mono<AccessToken> login(UserCredential userCredential) {
        return userRepository.findByEmail(userCredential.getEmail())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> validatePassword(user, userCredential.getPassword()))
                .flatMap(user -> rolRepository.findById(user.getRol().getId())
                        .map(Rol::getName)
                        .map(roleName -> buildToken(user, roleName))
                );
    }

    private Mono<User> validatePassword(User user, String rawPassword) {
        // implementar validación real de contraseña (ej: BCrypt)
        if ("1234".equals(rawPassword)) { // 👈 Ejemplo temporal
            return Mono.just(user);
        }
        return Mono.error(new IllegalArgumentException("Invalid password"));
    }

    private AccessToken buildToken(User user, String roleName) {
        String token = jwtRepository.generateToken(user, roleName);
        return new AccessToken(token);
    }
}
