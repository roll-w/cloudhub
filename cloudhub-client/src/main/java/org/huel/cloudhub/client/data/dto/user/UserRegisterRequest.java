package org.huel.cloudhub.client.data.dto.user;

/**
 * @author RollW
 */
public class UserRegisterRequest {
    private String username;
    private String email;

    private String password;
    // private String repeatPassword;

    public UserRegisterRequest() {
    }

    public UserRegisterRequest(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
