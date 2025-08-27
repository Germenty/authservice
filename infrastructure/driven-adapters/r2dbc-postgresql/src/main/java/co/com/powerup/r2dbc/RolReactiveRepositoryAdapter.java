package co.com.powerup.r2dbc;

import java.math.BigInteger;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.rol.gateways.RolRepository;
import co.com.powerup.r2dbc.entity.RolEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.RolReactiveRepository;
import reactor.core.publisher.Mono;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        BigInteger,
        RolReactiveRepository> implements RolRepository {

    private static final Logger log = LoggerFactory.getLogger(RolReactiveRepositoryAdapter.class);

    protected RolReactiveRepositoryAdapter(
            RolReactiveRepository repository,
            ObjectMapper mapper
    ) {
        super(repository, mapper, rolEntity -> Rol.builder()
                .id(rolEntity.getUniqueId() != null ? rolEntity.getUniqueId().toString() : null)
                .name(rolEntity.getName())
                .description(rolEntity.getDescription())
                .build()
        );
    }

    @Override
    public Mono<Rol> findById(String id) {
        return repository.findById(new BigInteger(id))
                .map(rolEntity -> Rol.builder()
                        .id(rolEntity.getUniqueId().toString())
                        .name(rolEntity.getName())
                        .description(rolEntity.getDescription())
                        .build()
                );
    }
}

