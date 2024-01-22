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
    private String provider;
    public static MemberResponseDto of(Member member) {
        if(member.getAddress() !=null)
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .password(member.getPassword())
                .address(member.getAddress().toAddressDto())
                .build();

        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .password(member.getPassword())
//                .address(member.getAddress().toAddressDto())
                .build();
    }


    // should not be used... because responseDTO will return already encoded password..
    // encoded password is not matchable with PasswordEncoder
//    public MemberRequestDto toMemberRequestDto(){
//        return MemberRequestDto.builder()
//                .provider(this.provider)
//                .username(this.username)
//                .email(this.email)
//                .password(this.password)
//                .address(this.address)
//                .build();
//    }
}
