package tinfo.project.tinfo482.functionalities.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.entity.Address;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

import static tinfo.project.tinfo482.functionalities.auth.entity.Role.ROLE_USER;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRequestDto {
    @NotNull(message = "username key doesn't exist")  // key값조차 없다
    @NotBlank(message = "email is required field")  // key는 넘어왔는데 value가 없다
    @Size(max = 100, message = "email exceeded the maximum length")
    private String email;
    @NotNull(message = "password key doesn't exist")
    private String password;
    private String username;
    private AddressDto address;
    private String provider;

    public Member toMember(PasswordEncoder passwordEncoder, Address address){
        return Member.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .username(this.username)
                .authority(ROLE_USER)
                .address(address)
                .provider(this.provider)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(PasswordEncoder passwordEncoder){
        // should not encode password... AuthenticationManager will encode by itself
        return new UsernamePasswordAuthenticationToken(username,password);
    }

}
