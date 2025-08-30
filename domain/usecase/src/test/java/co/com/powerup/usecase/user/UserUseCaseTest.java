package co.com.powerup.usecase.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.powerup.model.constants.UserConstants;
import co.com.powerup.model.rol.Rol;
import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCase Tests")
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private UserUseCase userUseCase;

    private User validUser;
    private User invalidUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository);

        validUser = User.builder()
                .name("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("+1234567890")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .rol(Rol.builder().build())
                .build();

        invalidUser = User.builder()
                .name("Jane")
                .lastName("Smith")
                .email("invalid-email")
                .baseSalary(new BigDecimal("20000000")) // Exceeds max
                .build();
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully when validation passes and email doesn't exist")
        void shouldCreateUserSuccessfully() {
            // Given
            when(userRepository.findByEmail(validUser.getEmail()))
                    .thenReturn(Mono.empty());
            when(userRepository.save(validUser))
                    .thenReturn(Mono.just(validUser));

            // When & Then
            StepVerifier.create(userUseCase.createUser(validUser))
                    .expectNext(validUser)
                    .verifyComplete();

            verify(userRepository).findByEmail(validUser.getEmail());
            verify(userRepository).save(validUser);
        }

        @Test
        @DisplayName("Should fail when user email already exists")
        void shouldFailWhenEmailAlreadyExists() {
            // Given
            User existingUser = validUser.toBuilder().build();
            when(userRepository.findByEmail(validUser.getEmail()))
                    .thenReturn(Mono.just(existingUser));

            // When & Then
            StepVerifier.create(userUseCase.createUser(validUser))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_EMAIL_EXISTS.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository).findByEmail(validUser.getEmail());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when user email is null")
        void shouldFailWhenEmailIsNull() {
            // Given
            User userWithNullEmail = validUser.toBuilder().email(null).build();

            // When & Then
            StepVerifier.create(userUseCase.createUser(userWithNullEmail))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_EMAIL_REQUIRED.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when user email is blank")
        void shouldFailWhenEmailIsBlank() {
            // Given
            User userWithBlankEmail = validUser.toBuilder().email("   ").build();

            // When & Then
            StepVerifier.create(userUseCase.createUser(userWithBlankEmail))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_EMAIL_REQUIRED.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when user email format is invalid")
        void shouldFailWhenEmailFormatIsInvalid() {
            // Given
            User userWithInvalidEmail = validUser.toBuilder().email("invalid-email").build();

            // When & Then
            StepVerifier.create(userUseCase.createUser(userWithInvalidEmail))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_EMAIL_INVALID.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when base salary is null")
        void shouldFailWhenBaseSalaryIsNull() {
            // Given
            User userWithNullSalary = validUser.toBuilder().baseSalary(null).build();

            // When & Then
            StepVerifier.create(userUseCase.createUser(userWithNullSalary))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_SALARY_REQUIRED.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when base salary is below minimum")
        void shouldFailWhenBaseSalaryIsBelowMinimum() {
            // Given
            User userWithLowSalary = validUser.toBuilder()
                    .baseSalary(BigDecimal.ZERO) // Equal to MIN (should be > 0)
                    .build();

            // When & Then
            StepVerifier.create(userUseCase.createUser(userWithLowSalary))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_SALARY_INVALID.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when base salary exceeds maximum")
        void shouldFailWhenBaseSalaryExceedsMaximum() {
            // Given
            User userWithHighSalary = validUser.toBuilder()
                    .baseSalary(new BigDecimal("20000000")) // Exceeds MAX_BASE_SALARY
                    .build();

            // When & Then
            StepVerifier.create(userUseCase.createUser(userWithHighSalary))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_SALARY_INVALID.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should handle repository save error")
        void shouldHandleRepositorySaveError() {
            // Given
            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(userRepository.findByEmail(validUser.getEmail()))
                    .thenReturn(Mono.empty());
            when(userRepository.save(validUser))
                    .thenReturn(Mono.error(repositoryError));

            // When & Then
            StepVerifier.create(userUseCase.createUser(validUser))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository).findByEmail(validUser.getEmail());
            verify(userRepository).save(validUser);
        }
    }

    @Nested
    @DisplayName("Get User By Email Tests")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Should return user when found by email")
        void shouldReturnUserWhenFoundByEmail() {
            // Given
            String email = "john.doe@example.com";
            when(userRepository.findByEmail(email))
                    .thenReturn(Mono.just(validUser));

            // When & Then
            StepVerifier.create(userUseCase.getUserByEmail(email))
                    .expectNext(validUser)
                    .verifyComplete();

            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // Given
            String email = "notfound@example.com";
            when(userRepository.findByEmail(email))
                    .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.getUserByEmail(email))
                    .verifyComplete();

            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should fail when email is null")
        void shouldFailWhenEmailIsNull() {
            // When & Then
            StepVerifier.create(userUseCase.getUserByEmail(null))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_EMAIL_REQUIRED.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Should fail when email is blank")
        void shouldFailWhenEmailIsBlank() {
            // When & Then
            StepVerifier.create(userUseCase.getUserByEmail("   "))
                    .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                            UserConstants.ERROR_EMAIL_REQUIRED.equals(throwable.getMessage()))
                    .verify();

            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Should handle repository error")
        void shouldHandleRepositoryError() {
            // Given
            String email = "john.doe@example.com";
            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(userRepository.findByEmail(email))
                    .thenReturn(Mono.error(repositoryError));

            // When & Then
            StepVerifier.create(userUseCase.getUserByEmail(email))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users when repository has data")
        void shouldReturnAllUsersWhenRepositoryHasData() {
            // Given
            User anotherUser = validUser.toBuilder()
                    .email("jane.doe@example.com")
                    .build();

            when(userRepository.findAll())
                    .thenReturn(Flux.just(validUser, anotherUser));

            // When & Then
            StepVerifier.create(userUseCase.getAllUsers())
                    .expectNext(validUser)
                    .expectNext(anotherUser)
                    .verifyComplete();

            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty flux when no users exist")
        void shouldReturnEmptyFluxWhenNoUsersExist() {
            // Given
            when(userRepository.findAll())
                    .thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(userUseCase.getAllUsers())
                    .verifyComplete();

            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should handle repository error")
        void shouldHandleRepositoryError() {
            // Given
            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(userRepository.findAll())
                    .thenReturn(Flux.error(repositoryError));

            // When & Then
            StepVerifier.create(userUseCase.getAllUsers())
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Remove User Tests")
    class RemoveUserTests {

        @Test
        @DisplayName("Should remove user successfully")
        void shouldRemoveUserSuccessfully() {
            // Given
            when(userRepository.delete(validUser))
                    .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userUseCase.removeUser(validUser))
                    .verifyComplete();

            verify(userRepository).delete(validUser);
        }

        @Test
        @DisplayName("Should handle repository delete error")
        void shouldHandleRepositoryDeleteError() {
            // Given
            RuntimeException repositoryError = new RuntimeException("Failed to delete user");
            when(userRepository.delete(validUser))
                    .thenReturn(Mono.error(repositoryError));

            // When & Then
            StepVerifier.create(userUseCase.removeUser(validUser))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository).delete(validUser);
        }
    }
}