package ng.prk.prkngandroid.model;

public class UserLoginData {
    private String email;
    private String password;
    private String type;
    private String access_token;

    public UserLoginData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginData(String email, String password, String type, String access_token) {
        this.email = email;
        this.password = password;
        this.type = type;
        this.access_token = access_token;
    }
}
