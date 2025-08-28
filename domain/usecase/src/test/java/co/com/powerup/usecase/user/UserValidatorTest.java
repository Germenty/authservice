package co.com.powerup.usecase.user;


import co.com.powerup.model.constants.UserConstants;
import co.com.powerup.model.user.User;
import co.com.powerup.usecase.user.validation.UserValidator;
import co.com.powerup.model.rol.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

@DisplayName("UserValidator Tests")
class UserValidatorTest {

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .userId("1")
                .name("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("+1234567890")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .rol(Rol.builder().build())
                .build();
    }

    @Test
    @DisplayName("Should validate successfully when all fields are valid")
    void shouldValidateSuccessfullyWhenAllFieldsAreValid() {
        // When & Then
        StepVerifier.create(UserValidator.validate(validUser))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate successfully with minimum valid salary")
    void shouldValidateSuccessfullyWithMinimumValidSalary() {
        // Given
        User userWithMinSalary = validUser.toBuilder()
                .baseSalary(new BigDecimal("0.01")) // Just above minimum
                .build();

        // When & Then
        StepVerifier.create(UserValidator.validate(userWithMinSalary))
                .expectNext(userWithMinSalary)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate successfully with maximum valid salary")
    void shouldValidateSuccessfullyWithMaximumValidSalary() {
        // Given
        User userWithMaxSalary = validUser.toBuilder()
                .baseSalary(UserConstants.MAX_BASE_SALARY) // Exactly at maximum
                .build();

        // When & Then
        StepVerifier.create(UserValidator.validate(userWithMaxSalary))
                .expectNext(userWithMaxSalary)
                .verifyComplete();
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should fail validation when email is null, empty or blank")
        void shouldFailValidationWhenEmailIsNullEmptyOrBlank(String email) {
            // Given
            User userWithInvalidEmail = validUser.toBuilder().email(email).build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithInvalidEmail))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_EMAIL_REQUIRED.equals(throwable.getMessage())
                    )
                    .verify();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-email",
            "@example.com",
            "user@",
            "user.example.com",
            "user@.com",
            "user@example.",
            "user space@example.com",
            "user@ex ample.com",
            ".user@example.com",
            "user.@example.com",
            "user@example..com",
            "user@@example.com"
        })
        @DisplayName("Should fail validation when email format is invalid")
        void shouldFailValidationWhenEmailFormatIsInvalid(String invalidEmail) {
            // Given
            User userWithInvalidEmail = validUser.toBuilder().email(invalidEmail).build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithInvalidEmail))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_EMAIL_INVALID.equals(throwable.getMessage())
                    )
                    .verify();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.com",
            "user_name@example.com",
            "user123@example123.com",
            "test@sub.example.com",
            "user@example.co.uk",
            "a@b.co",
            "very.long.email.address@very.long.domain.name.com"
        })
        @DisplayName("Should validate successfully when email format is valid")
        void shouldValidateSuccessfullyWhenEmailFormatIsValid(String validEmail) {
            // Given
            User userWithValidEmail = validUser.toBuilder().email(validEmail).build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithValidEmail))
                    .expectNext(userWithValidEmail)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Base Salary Validation Tests")
    class BaseSalaryValidationTests {

        @Test
        @DisplayName("Should fail validation when base salary is null")
        void shouldFailValidationWhenBaseSalaryIsNull() {
            // Given
            User userWithNullSalary = validUser.toBuilder().baseSalary(null).build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithNullSalary))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_SALARY_REQUIRED.equals(throwable.getMessage())
                    )
                    .verify();
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0", "-100", "-0.01"})
        @DisplayName("Should fail validation when base salary is less than or equal to minimum")
        void shouldFailValidationWhenBaseSalaryIsLessThanOrEqualToMinimum(String salaryValue) {
            // Given
            User userWithLowSalary = validUser.toBuilder()
                    .baseSalary(new BigDecimal(salaryValue))
                    .build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithLowSalary))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_SALARY_INVALID.equals(throwable.getMessage())
                    )
                    .verify();
        }

        @ParameterizedTest
        @ValueSource(strings = {"15000001", "20000000", "15000000.01"})
        @DisplayName("Should fail validation when base salary exceeds maximum")
        void shouldFailValidationWhenBaseSalaryExceedsMaximum(String salaryValue) {
            // Given
            User userWithHighSalary = validUser.toBuilder()
                    .baseSalary(new BigDecimal(salaryValue))
                    .build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithHighSalary))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_SALARY_INVALID.equals(throwable.getMessage())
                    )
                    .verify();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.01", "1", "100", "5000000", "10000000", "15000000"})
        @DisplayName("Should validate successfully when base salary is within valid range")
        void shouldValidateSuccessfullyWhenBaseSalaryIsWithinValidRange(String salaryValue) {
            // Given
            User userWithValidSalary = validUser.toBuilder()
                    .baseSalary(new BigDecimal(salaryValue))
                    .build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithValidSalary))
                    .expectNext(userWithValidSalary)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Multiple Validation Errors Tests")
    class MultipleValidationErrorsTests {

        @Test
        @DisplayName("Should fail with email error when both email and salary are invalid")
        void shouldFailWithEmailErrorWhenBothEmailAndSalaryAreInvalid() {
            // Given - Email validation comes first in the validator
            User userWithMultipleErrors = validUser.toBuilder()
                    .email(null)
                    .baseSalary(new BigDecimal("-1"))
                    .build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithMultipleErrors))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_EMAIL_REQUIRED.equals(throwable.getMessage())
                    )
                    .verify();
        }

        @Test
        @DisplayName("Should fail with salary error when email is valid but salary is invalid")
        void shouldFailWithSalaryErrorWhenEmailIsValidButSalaryIsInvalid() {
            // Given
            User userWithInvalidSalary = validUser.toBuilder()
                    .baseSalary(null)
                    .build();

            // When & Then
            StepVerifier.create(UserValidator.validate(userWithInvalidSalary))
                    .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        UserConstants.ERROR_SALARY_REQUIRED.equals(throwable.getMessage())
                    )
                    .verify();
        }
    }

    @Test
    @DisplayName("Should throw exception when trying to instantiate UserValidator")
    void shouldThrowExceptionWhenTryingToInstantiateUserValidator() {
        // When & Then
        StepVerifier.create(Mono.fromCallable(() -> {
            try {
                return UserValidator.class.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }))
        .expectError(IllegalStateException.class)
        .verify();
    }
}
