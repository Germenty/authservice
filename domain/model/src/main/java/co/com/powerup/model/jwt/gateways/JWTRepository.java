package co.com.powerup.model.jwt.gateways;

import co.com.powerup.model.user.User;

public interface JWTRepository {

    String generateToken(User user, String userRole);

    Boolean validateToken(String token);

    String extractUserEmail(String token);

    String extractRole(String token);

}
