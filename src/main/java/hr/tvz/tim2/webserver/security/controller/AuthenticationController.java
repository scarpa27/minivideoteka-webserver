package hr.tvz.tim2.webserver.security.controller;

import hr.tvz.tim2.webserver.security.command.LoginCommand;
import hr.tvz.tim2.webserver.security.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final LoginCommand command) {
         var loginDto = authenticationService.login(command);

         if (loginDto.isPresent()) {
             return ResponseEntity.ok().body(loginDto.get());
         } else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
         }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody final LoginCommand command) {

        if (authenticationService.userExists(command))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (authenticationService.register(command))
            return ResponseEntity.status(HttpStatus.OK).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}