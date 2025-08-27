package co.com.powerup.model.rol.gateways;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.com.powerup.model.rol.Rol;

public interface RolRepository {

    Mono<Rol> save(Rol rol);

    Mono<Rol> findById(String id);

    Flux<Rol> findAll();

}
