package hr.tvz.tim2.webserver.membership;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/membership")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class MembershipController {
    MemberService memberService;

    public MembershipController(@Autowired MemberService memberService) {
        this.memberService = memberService;
    }

    @PutMapping
    public ResponseEntity<MembershipDto> becomeMember(@Valid @RequestBody MemberCommand request,
                                                      @AuthenticationPrincipal ApplicationUser user) {
        memberService.activateMember(request, user);
        return getUserMembership(user);
    }

    @GetMapping
    public ResponseEntity<MembershipDto> getUserMembership(@AuthenticationPrincipal ApplicationUser user) {
        MembershipDto dto = toDto(memberService.getMemberEntity(user.getUsername()));
        return ResponseEntity.ok().body(dto);
    }
}
