package co.com.powerup.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import co.com.powerup.r2dbc.entity.UserEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

public class UserReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository> implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return null;
    }

    @Override
    public Mono<Void> delete(User user) {
        return null;
    }
}
