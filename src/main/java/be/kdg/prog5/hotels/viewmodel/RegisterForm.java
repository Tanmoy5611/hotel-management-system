package be.kdg.prog5.hotels.viewmodel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// used for creating a new user from the form
public class RegisterForm {

    // email must be valid and not empty
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public String getEmail() {
        return email;
    }

    // setter for email (used when form submits data)
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