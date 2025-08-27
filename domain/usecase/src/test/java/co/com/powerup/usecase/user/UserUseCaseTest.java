package co.com.powerup.usecase.user;


import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userUseCase = new UserUseCase(userRepository);
    }

    @Test
    void saveUser_ShouldReturnSavedUser() {
        User user = User.builder()
                .userId("123")
                .name("Daniel")
                .lastName("Martin")
                .email("daniel@example.com")
                .birthDate(LocalDate.of(1990,5,15))
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .rol(new Rol())
                .baseSalary(BigDecimal.valueOf(3500000))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().equals("daniel@example.com"))
                .verifyComplete();

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserByEmail_ShouldReturnUser() {
        User user = User.builder()
                .userId("123")
                .name("Daniel")
                .email("daniel@example.com")
                .build();

        when(userRepository.findByEmail("daniel@example.com")).thenReturn(Mono.just(user));

        Mono<User> result = userUseCase.findUserByEmail("daniel@example.com");

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getName().equals("Daniel"))
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail("daniel@example.com");
    }

    @Test
    void findUserByEmail_ShouldReturnEmptyIfNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Mono.empty());

        Mono<User> result = userUseCase.findUserByEmail("notfound@example.com");

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }
}