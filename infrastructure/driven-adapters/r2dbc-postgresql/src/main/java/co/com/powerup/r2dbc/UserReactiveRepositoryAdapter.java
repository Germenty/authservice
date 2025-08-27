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

    private final UserReactiveRepository repository;
    private final ObjectMapper mapper;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> User.builder()
                .userId(entity.getUserId().toString())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .address(entity.getAddress())
                .birthDate(entity.getBirthDate())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .baseSalary(entity.getBaseSalary())
                .build()
        );
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(entity -> User.builder()
                        .userId(entity.getUserId().toString())
                        .name(entity.getName())
                        .lastName(entity.getLastName())
                        .address(entity.getAddress())
                        .birthDate(entity.getBirthDate())
                        .phoneNumber(entity.getPhoneNumber())
                        .email(entity.getEmail())
                        .baseSalary(entity.getBaseSalary())
                        .build()
                );
    }

    @Override
    public Mono<Void> delete(User user) {
        return repository.deleteById(UUID.fromString(user.getUserId()));
    }
}

