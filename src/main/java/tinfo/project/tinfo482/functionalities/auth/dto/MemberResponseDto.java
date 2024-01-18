package tinfo.project.tinfo482.functionalities.auth.dto;

import lombok.*;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private String email;
    private String username;
    private String password;
    private AddressDto address;
    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .password(member.getPassword())
                .address(member.getAddress().toAddressDto())
                .build();
    }
}
