package co.com.powerup.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

@Table("auth.users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column("user_id")
    private UUID userId;

    private String name;
    private String lastName;
    private String address;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;

    @Column("rol_id")
    private BigInteger rolId;

    private BigDecimal baseSalary;
}

