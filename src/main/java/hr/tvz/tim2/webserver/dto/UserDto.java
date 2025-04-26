package hr.tvz.tim2.webserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hr.tvz.tim2.webserver.membership.MembershipDto;
import lombok.Data;

@Data
public class UserDto {
    @JsonProperty(index = 1)
    private Long id;
    @JsonProperty(index = 2)
    private String username;
    @JsonProperty(index = 3)
    private String authorities;

    @JsonProperty(index = 4)
    private int ordersCount;
    @JsonProperty(index = 5)
    private boolean hasActiveOrder;

    @JsonProperty(index = 6)
    private int reviewsCount;

    @JsonProperty(index = 7)
    private boolean isBanned;

    @JsonProperty(index = 8)
    private boolean isMember;
    @JsonProperty(index = 9)
    private MembershipDto membershipDto;


}
