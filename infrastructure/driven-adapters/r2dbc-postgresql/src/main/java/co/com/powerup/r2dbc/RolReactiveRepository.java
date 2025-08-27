package co.com.powerup.r2dbc;

import co.com.powerup.r2dbc.entity.RolEntity;

import java.math.BigInteger;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, BigInteger>, ReactiveQueryByExampleExecutor<RolEntity> {

}
