package hr.tvz.tim2.webserver.admin;

import java.time.Duration;
import java.time.Instant;

public class BanDto {
    public Long userId;
    public String reason;
    public Duration duration;
    public Instant expiration;
}
