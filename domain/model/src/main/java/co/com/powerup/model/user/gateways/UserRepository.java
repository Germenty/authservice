package co.com.powerup.model.user.gateways;

import co.com.powerup.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);

    Mono<User> findByEmail(String email);
}
