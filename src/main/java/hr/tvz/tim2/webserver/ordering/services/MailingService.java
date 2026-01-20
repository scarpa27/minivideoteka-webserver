package hr.tvz.tim2.webserver.ordering.services;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@NoArgsConstructor
public class MailingService {
    private static final Random random = new Random();

    public String generateTrackingNumber(int packageSize) {
        log.debug("Generating tracking number for {} package size", packageSize);
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

        log.debug("Generated tracking number {}", sb);
        return sb.toString();
    }
}
