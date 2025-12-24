package hr.tvz.tim2.webserver.ordering.services;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@NoArgsConstructor
public class MailingService {
    public String generateTrackingNumber(int packageSize) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 2; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            sb.append(letter);
        }

        // Add 8 random digits
        for (int i = 0; i < 8; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }
}
