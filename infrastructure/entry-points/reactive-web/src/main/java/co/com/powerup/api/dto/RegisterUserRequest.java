package co.com.powerup.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterUserRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name cannot exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotBlank(message = "Address is required")
        @Size(max = 100, message = "Address cannot exceed 100 characters")
        String address,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Phone number must be valid")
        String phoneNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Role ID is required")
        Long roleId,

        @NotNull(message = "Base salary is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
        BigDecimal baseSalary
) {}
