package tinfo.project.tinfo482.functionalities.auth.dto;

import lombok.*;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private String email;
    private String username;
    private String password;
    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .password(member.getPassword())
                .build();
    }
}
