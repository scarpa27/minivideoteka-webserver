package hr.tvz.tim2.webserver.security.controller;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.security.command.LoginCommand;
import hr.tvz.tim2.webserver.security.dto.LoginDTO;
import hr.tvz.tim2.webserver.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody final LoginCommand command) {
         var loginDto = authenticationService.login(command);
         if (loginDto.isPresent())
             return ResponseEntity.ok().body(loginDto.get());
         else
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody final LoginCommand command) {

        if (authenticationService.userExists(command))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists!");
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (authenticationService.register(command))
            return ResponseEntity.status(HttpStatus.OK).build();

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");

//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody final LoginCommand command) {

        if (authenticationService.userExists(command))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (authenticationService.registerAdmin(command))
            return ResponseEntity.status(HttpStatus.OK).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}