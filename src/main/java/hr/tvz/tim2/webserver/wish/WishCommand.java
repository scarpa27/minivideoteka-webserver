package hr.tvz.tim2.webserver.wish;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishCommand {
    @Pattern(regexp = "^tt\\d{7,9}$", message = "Invalid IMDb ID format")
    private String imdbId;

    @NotBlank(message = "Message cannot be empty!")
    private String message;
}
