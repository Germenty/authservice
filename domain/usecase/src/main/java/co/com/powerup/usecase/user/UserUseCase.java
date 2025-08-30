package co.com.powerup.usecase.user;

import java.util.NoSuchElementException;

import co.com.powerup.model.constants.UserConstants;
import co.com.powerup.model.rol.gateways.RolRepository;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import co.com.powerup.usecase.user.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public Mono<User> createUser(User user) {
        return UserValidator.validate(user)
                // 1. Verificar email único
                .flatMap(validUser -> userRepository.findByEmail(validUser.getEmail())
                        .flatMap(existing -> Mono.<User>error(
                                new IllegalArgumentException(UserConstants.ERROR_EMAIL_EXISTS)))
                        // 2. Si no existe, cargar el rol
                        .switchIfEmpty(
                                rolRepository.findById(validUser.getRol().getId())
                                        .switchIfEmpty(Mono.error(
                                                new IllegalArgumentException("Rol no encontrado")))
                                        // 3. Asignar el rol completo al usuario validado
                                        .map(rol -> validUser.toBuilder().rol(rol).build())
                                        // 4. Guardar el usuario con el rol ya incluido
                                        .flatMap(userConRol -> userRepository.save(userConRol))));
    }

    public Mono<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Mono.error(new IllegalArgumentException("Email es requerido"));
        }

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Usuario no encontrado")))
                .flatMap(user -> rolRepository.findById(user.getRol().getId())
                        .map(rol -> user.toBuilder() // tu dominio usa toBuilder = true
                                .rol(rol) // reemplazamos solo el Rol
                                .build()));
    }
}
