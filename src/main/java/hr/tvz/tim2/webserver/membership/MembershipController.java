package hr.tvz.tim2.webserver.membership;

import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/membership")
public class MembershipController {
    MemberService memberService;

    public MembershipController(@Autowired MemberService memberService) {
        this.memberService = memberService;
    }

    @PutMapping
    public ResponseEntity<?> becomeMember(@Valid @RequestBody MemberCommand request,
                                          @AuthenticationPrincipal ApplicationUser user) {
        try {
            memberService.activateMember(request, user);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(getUserMembership(user));
    }

    @GetMapping
    public ResponseEntity<?> getUserMembership(@AuthenticationPrincipal ApplicationUser user) {
        try {
            var dto = toDto(memberService.getMemberEntity(user.getUsername()));
            return ResponseEntity.ok().body(dto);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("User is not a member!\n"+e.getMessage());
        }
    }
}
