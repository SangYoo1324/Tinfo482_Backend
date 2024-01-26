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
    // (not for returning to frontend server.) internal use only. will expose password
    public static MemberResponseDto of(Member member) {

        // preventing address null-pointer
        if(member.getAddress() !=null)
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .password(member.getPassword())
                //if member.getAddress = null, nullpointer exception because ref var is null
                .address(member.getAddress().toAddressDto())
                .build();

        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .password(member.getPassword())
//                .address(member.getAddress().toAddressDto())
                .build();
    }

    //external use without password
    public static MemberResponseDto secureMemberResponseDto(Member member){

        if(member.getAddress() !=null)
      return   MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .address(member.getAddress().toAddressDto())
                 .provider(member.getProvider())
                .build();
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
//                .address(member.getAddress().toAddressDto())
                .provider(member.getProvider())
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
