package hr.tvz.tim2.webserver.security.dto;

import java.util.List;

public record LoginDTO(String jwt, List<String> authorities) {
}
