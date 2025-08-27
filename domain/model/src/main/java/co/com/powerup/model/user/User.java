package co.com.powerup.model.user;

import lombok.Builder;

import co.com.powerup.model.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// @NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String userId;
    private String name;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;
    private Rol rol;
    private Double baseSalary;

}
