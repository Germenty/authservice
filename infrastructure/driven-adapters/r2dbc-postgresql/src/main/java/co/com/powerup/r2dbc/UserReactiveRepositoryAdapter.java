package co.com.powerup.r2dbc;

import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import co.com.powerup.r2dbc.entity.UserEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.UserReactiveRepository;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, UUID, UserReactiveRepository>
        implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapToDomain(entity));
        this.mapper = mapper;
        log.info("UserReactiveRepositoryAdapter initialized with repository={} and mapper={}",
                repository.getClass().getSimpleName(), mapper.getClass().getSimpleName());
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.info("Searching for user by email: {}", email);
        return repository.findByEmail(email)
                .map(UserReactiveRepositoryAdapter::mapToDomain)
                .doOnNext(user -> log.info("Found user: userId={}, email={}", user.getUserId(), user.getEmail()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No user found with email: {}", email);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Void> delete(User user) {
        UUID userId = UUID.fromString(user.getUserId());
        log.info("Deleting user: userId={}, email={}", userId, user.getEmail());
        return repository.deleteById(userId)
                .doOnSuccess(v -> log.info("User deleted successfully: userId={}", userId))
                .doOnError(e -> log.error("Error deleting user: userId={}", userId, e));
    }

    // Map entity to domain
    private static User mapToDomain(UserEntity entity) {
        return User.builder()
                .userId(entity.getUserId().toString())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .address(entity.getAddress())
                .birthDate(entity.getBirthDate())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .baseSalary(entity.getBaseSalary())
                .build();
    }
}
