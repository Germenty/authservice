package co.com.powerup.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterUserResponse(

        String userId,

        String name,

        String lastName,

        LocalDate birthDate,

        String address,

        String phoneNumber,

        String email,

        String roleName, // Instead of returning the entire Rol object, return just its name or description

        BigDecimal baseSalary
) {}
