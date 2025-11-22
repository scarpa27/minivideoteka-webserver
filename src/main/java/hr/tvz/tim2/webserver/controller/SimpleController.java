package hr.tvz.tim2.webserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping
public class SimpleController {

    @GetMapping
    public List<String> showWelcomeMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Welcome to Tim2!");
        messages.add("This is a simple web server application.");
        messages.add("If you can see this message, API has been set up correctly");
        return messages;
    }
}
