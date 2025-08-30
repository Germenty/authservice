package co.com.powerup.r2dbc;

import java.math.BigInteger;
import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.rol.Rol;
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
        log.info("UserReactiveRepositoryAdapter initialized with repository={} and mapper={}",
                repository.getClass().getSimpleName(),
                mapper.getClass().getSimpleName());
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.info("Searching for user by email: {}", email);
        return repository.findByEmail(email)
                .map(UserReactiveRepositoryAdapter::mapToDomain)
                .doOnNext(user -> log.info("Found user: email={}, rolId={}",
                        user.getEmail(),
                        user.getRol().getId()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No user found with email: {}", email);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<User> save(User user) {
        log.info("Saving user: email={}, rolId={}",
                user.getEmail(),
                user.getRol().getId());

        UserEntity entity = mapToEntity(user);
        return repository.save(entity)
                .map(savedEntity -> {
                    User mapped = mapToDomain(savedEntity);
                    // Restaurar el objeto Rol tal como llegó en el dominio
                    mapped.setRol(user.getRol());
                    return mapped;
                });
    }

    // Mapea UserEntity → User (incluye al menos el id del rol)
    private static User mapToDomain(UserEntity userEntity) {
        Rol rol = Rol.builder()
                .id(userEntity.getRolId() != null ? userEntity.getRolId().toString() : null)
                .build();

        return User.builder()
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .address(userEntity.getAddress())
                .birthDate(userEntity.getBirthDate())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .baseSalary(userEntity.getBaseSalary())
                .rol(rol)
                .build();
    }

    // Mapea User → UserEntity (incluye rolId)
    private static UserEntity mapToEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setName(user.getName());
        entity.setLastName(user.getLastName());
        entity.setAddress(user.getAddress());
        entity.setBirthDate(user.getBirthDate());
        entity.setPhoneNumber(user.getPhoneNumber());
        entity.setEmail(user.getEmail());
        entity.setBaseSalary(user.getBaseSalary());

        // Asignar rolId si viene en el dominio
        if (user.getRol() != null && user.getRol().getId() != null) {
            entity.setRolId(new BigInteger(user.getRol().getId()));
        }
        return entity;
    }
}