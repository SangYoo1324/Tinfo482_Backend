package tinfo.project.tinfo482.functionalities.auth.dto;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import tinfo.project.tinfo482.entity.Address;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

import static tinfo.project.tinfo482.functionalities.auth.entity.Role.ROLE_USER;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRequestDto {

    private String email;
    private String password;
    private String username;
    private Address  address;

    public Member toMember(PasswordEncoder passwordEncoder, Address address){
        return Member.builder()
                .email(this.email)
                .password(passwordEncoder.encode(password))
                .username(username)
                .authority(ROLE_USER)
                .address(address)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(PasswordEncoder passwordEncoder){
        // should not encode password... AuthenticationManager will encode by itself
        return new UsernamePasswordAuthenticationToken(username,password);
    }

}
