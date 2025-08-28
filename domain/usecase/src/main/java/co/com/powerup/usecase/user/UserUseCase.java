package co.com.powerup.usecase.user;

import co.com.powerup.model.constants.UserConstants;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import co.com.powerup.usecase.user.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return UserValidator.validate(user)
                .flatMap(validUser -> userRepository.findByEmail(validUser.getEmail())
                        .flatMap(existingUser -> Mono.<User>error(new IllegalArgumentException(UserConstants.ERROR_EMAIL_EXISTS)))
                        .switchIfEmpty(userRepository.save(validUser)));

    }

    public Mono<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Mono.error(new IllegalArgumentException(UserConstants.ERROR_EMAIL_REQUIRED));
        }
        return userRepository.findByEmail(email);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<Void> removeUser(User user) {
        return userRepository.delete(user);
    }
}
