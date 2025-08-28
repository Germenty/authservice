package co.com.powerup.usecase.user.validation;

import co.com.powerup.model.constants.UserConstants;
import co.com.powerup.model.user.User;
import reactor.core.publisher.Mono;

public class UserValidator {

    private UserValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static Mono<User> validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return Mono.error(new IllegalArgumentException(UserConstants.ERROR_EMAIL_REQUIRED));
        }
        if (user.getBaseSalary() == null) {
            return Mono.error(new IllegalArgumentException(UserConstants.ERROR_SALARY_REQUIRED));
        }
        if (user.getBaseSalary().compareTo(UserConstants.MIN_BASE_SALARY) <= 0
                || user.getBaseSalary().compareTo(UserConstants.MAX_BASE_SALARY) > 0) {
            return Mono.error(new IllegalArgumentException(UserConstants.ERROR_SALARY_INVALID));
        }
        if (!user.getEmail().matches(UserConstants.EMAIL_REGEX)) {
            return Mono.error(new IllegalArgumentException(UserConstants.ERROR_EMAIL_INVALID));
        }
        return Mono.just(user);
    }
}
