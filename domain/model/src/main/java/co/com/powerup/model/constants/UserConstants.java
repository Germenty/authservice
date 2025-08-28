package co.com.powerup.model.constants;


import java.math.BigDecimal;

public final class UserConstants {

    private UserConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final BigDecimal MIN_BASE_SALARY = BigDecimal.ZERO;
    public static final BigDecimal MAX_BASE_SALARY = new BigDecimal("15000000");

    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static final String ERROR_EMAIL_REQUIRED = "Email is required";
    public static final String ERROR_SALARY_REQUIRED = "Base salary is required";
    public static final String ERROR_SALARY_INVALID = "Base salary must be between 0 and 15,000,000";
    public static final String ERROR_EMAIL_INVALID = "Email format is invalid";
    public static final String ERROR_EMAIL_EXISTS = "Email is already registered";
}