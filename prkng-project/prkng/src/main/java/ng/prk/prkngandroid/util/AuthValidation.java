package ng.prk.prkngandroid.util;

public class AuthValidation {
    private static final String TAG = "AuthValidation";

    private static final String PATTERN_USERNAME = "^[a-zA-Z0-9\\-]*$"; // alpha-numeric

    private static final int MIN_LENGTH_NAME = 2;       // Also see: R.string.auth_error_invalid_name
    private static final int MIN_LENGTH_PASSWORD = 6;

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        email = email.trim();

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        name = name.trim();

        return (name.length() >= MIN_LENGTH_NAME);
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }

        return (password.length() >= MIN_LENGTH_PASSWORD);
    }
}
