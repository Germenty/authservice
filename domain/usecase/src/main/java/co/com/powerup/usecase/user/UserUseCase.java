package co.com.powerup.usecase.user;

import co.com.powerup.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.com.powerup.model.user.User;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<Void> deleteUser(User user) {
        return userRepository.delete(user);
    }

}
