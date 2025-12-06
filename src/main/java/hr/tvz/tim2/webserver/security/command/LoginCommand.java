package hr.tvz.tim2.webserver.security.command;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

@Getter
public class LoginCommand {
    @NotBlank(message = "Username must not be empty")
    private String username;

    @NotBlank(message = "Password must not be empty")
    private String password;
}
