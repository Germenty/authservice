package co.com.powerup.usecase.rol;

import co.com.powerup.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import co.com.powerup.model.rol.Rol;

@RequiredArgsConstructor
public class RolUseCase {

    private final RolRepository rolRepository;

    public Flux<Rol> findAllRoles() {
        return rolRepository.findAll();
    }

}
