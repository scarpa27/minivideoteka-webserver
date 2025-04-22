package hr.tvz.tim2.webserver.membership;

import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MemberService {


    private final UserRepository userDbRepository;
    private final MemberDbRepository memberDbRepository;

    public MemberService(@Autowired UserRepository userDbRepository,
                         @Autowired MemberDbRepository memberDbRepository) {
        this.userDbRepository = userDbRepository;
        this.memberDbRepository = memberDbRepository;
    }

    public MemberEntity getMemberEntity(String username) {
        return memberDbRepository.findByUser_Id(getUserId(username)).orElseThrow(() -> new IllegalStateException("User is not member!"));
    }

    public void activateMember(MemberCommand request,
                               ApplicationUser user) {
        var userId = getUserId(user.getUsername());

        CardInfo card = new CardInfo();
        card.setCardNumber(mask(request.getCardNumber())); // save only last 4
        card.setCardExpirationDate(request.getCardExpirationDate());
        card.setCardHolderName(request.getCardHolderName());

        ShippingInfo address = new ShippingInfo();
        address.setStreetWithNumber(request.getStreetWithNumber());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());

        if (isUserMember(userId)) {
            var member = memberDbRepository.findByUser_Id(userId).orElseThrow(() -> new IllegalStateException("User is not member!"));
            member.setCardInfo(card);
            member.setShippingInfo(address);
            if (!isUserActiveMember(userId)) {
                member.setIsActivated(true);
                member.setValidUntil(Instant.now().plusSeconds(60 * 60 * 24 * 366));
            } else {
                member.setIsActivated(false);
            }
            memberDbRepository.saveAndFlush(member);
            System.out.println("Updated member info for user " + user.getUsername());
        }
        else {
            var entity = new MemberEntity();
            entity.setCardInfo(card);
            entity.setShippingInfo(address);
            entity.setUser(userDbRepository.getReferenceById(userId));
            entity.setIsActivated(true);
            entity.setValidUntil(Instant.now().plusSeconds(60 * 60 * 24 * 366));
            memberDbRepository.saveAndFlush(entity);
            System.out.println("Created member info for user " + user.getUsername());
        }
    }

    public boolean isUserActiveMember(Long userId) {
        var optionalMember = memberDbRepository.findByUser_Id(userId);

        if (optionalMember.isEmpty())
            return false;

        var member = optionalMember.get();

        return member.getValidUntil().isAfter(Instant.now());
    }

    public boolean isUserMember(Long userId) {
        var optionalMember = memberDbRepository.findByUser_Id(userId);
        return optionalMember.isPresent();
    }

    private String mask(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    private Long getUserId(String userName) {
        return userDbRepository.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!"))
                .getId();
    }
}
