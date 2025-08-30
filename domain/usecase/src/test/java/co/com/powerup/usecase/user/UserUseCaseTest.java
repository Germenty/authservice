package co.com.powerup.usecase.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.powerup.model.constants.UserConstants;
import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.rol.gateways.RolRepository;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;
    private Rol fullRol;

    @BeforeEach
    void setUp() {
        // Rol completo desde la BD
        fullRol = Rol.builder()
                .id("1")
                .name("ADMIN")
                .description("Administrador de servicio")
                .build();

        // Usuario de dominio con sólo rol.id (antes de enriquecer)
        validUser = User.builder()
                .name("Daniel")
                .lastName("Martin")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Calle 123 #45-67")
                .phoneNumber("+573001234567")
                .email("daniel@example.com")
                .baseSalary(BigDecimal.valueOf(5_000_000))
                .rol(Rol.builder().id("1").build())
                .build();
    }

    // 1. createUser – éxito: email no existe y rol sí existe
    @Test
    void whenCreateUserAndEmailNotExistsAndRolFound_thenReturnsSavedUser() {
        // dado
        User enrichedUser = validUser.toBuilder().rol(fullRol).build();
        given(userRepository.findByEmail(validUser.getEmail()))
                .willReturn(Mono.empty());
        given(rolRepository.findById("1"))
                .willReturn(Mono.just(fullRol));
        given(userRepository.save(any(User.class)))
                .willReturn(Mono.just(enrichedUser));

        // cuando & entonces
        StepVerifier.create(userUseCase.createUser(validUser))
                .expectNextMatches(user -> user.getEmail().equals(validUser.getEmail()) &&
                        user.getRol().getName().equals("ADMIN") &&
                        user.getRol().getDescription().equals("Administrador de servicio"))
                .verifyComplete();
    }

    // 2. createUser – falla por email ya existente
    @Test
    void whenCreateUserAndEmailExists_thenIllegalArgumentException() {
        given(userRepository.findByEmail(validUser.getEmail()))
                .willReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException
                        && ex.getMessage().equals(UserConstants.ERROR_EMAIL_EXISTS))
                .verify();
    }

    // 3. createUser – falla porque no existe el rol
    @Test
    void whenCreateUserAndRolNotFound_thenIllegalArgumentException() {
        given(userRepository.findByEmail(validUser.getEmail()))
                .willReturn(Mono.empty());
        given(rolRepository.findById("1"))
                .willReturn(Mono.empty());

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException
                        && ex.getMessage().equals("Rol no encontrado"))
                .verify();
    }

    // 4. getUserByEmail – falla por email blank
    @Test
    void whenGetUserByEmailWithBlank_thenIllegalArgumentException() {
        StepVerifier.create(userUseCase.getUserByEmail(" "))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException
                        && ex.getMessage().equals("Email es requerido"))
                .verify();
    }

    // 5. getUserByEmail – falla porque no existe el usuario
    @Test
    void whenGetUserByEmailNotFound_thenNoSuchElementException() {
        given(userRepository.findByEmail(validUser.getEmail()))
                .willReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserByEmail(validUser.getEmail()))
                .expectErrorMatches(ex -> ex instanceof NoSuchElementException
                        && ex.getMessage().equals("Usuario no encontrado"))
                .verify();
    }

    // 6. getUserByEmail – éxito: usuario existe y rol se carga luego
    @Test
    void whenGetUserByEmailAndRolFound_thenReturnsUserWithFullRol() {
        // usuario con solo rol.id
        User userWithIdOnlyRol = validUser.toBuilder().rol(Rol.builder().id("1").build()).build();

        given(userRepository.findByEmail(validUser.getEmail()))
                .willReturn(Mono.just(userWithIdOnlyRol));
        given(rolRepository.findById("1"))
                .willReturn(Mono.just(fullRol));

        StepVerifier.create(userUseCase.getUserByEmail(validUser.getEmail()))
                .expectNextMatches(user -> user.getEmail().equals(validUser.getEmail()) &&
                        user.getRol().getName().equals("ADMIN") &&
                        user.getRol().getDescription().equals("Administrador de servicio"))
                .verifyComplete();
    }
}