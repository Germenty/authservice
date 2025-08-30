package co.com.powerup.model.rol.gateways;

import co.com.powerup.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {

    Mono<Rol> findById(String id);
    
}
