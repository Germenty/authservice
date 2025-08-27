package co.com.powerup.r2dbc;

import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.rol.gateways.RolRepository;
import co.com.powerup.r2dbc.entity.RolEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.RolReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.function.Function;

@Repository
public  class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        BigInteger,
        RolReactiveRepository> implements RolRepository{

    private static final Logger log = LoggerFactory.getLogger(RolReactiveRepositoryAdapter.class);

    protected RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper, Function<RolEntity, Rol> toEntityFn) {
        super(repository, mapper, toEntityFn);
    }


    @Override
    public Mono<Rol> findById(String id) {
        return null;
    }
}
