package hr.tvz.tim2.webserver.review;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommand {
    @NotBlank(message = "Review text cannot be empty!")
    private String text;
}
