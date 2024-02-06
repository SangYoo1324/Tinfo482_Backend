package tinfo.project.tinfo482.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.entity.Address;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberRequestDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberResponseDto;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.functionalities.auth.service.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class AddressServiceTest {

    @Autowired
    AddressService addressService;

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void getAddress() {
       MemberResponseDto member =  authService.signup(
                MemberRequestDto.builder()
                        .username("sam")
                        .email("sammyyoo1324@gmail.com")
                        .password("aaa")
                        .build()
        );


        try {
           AddressDto address= addressService.getAddress(member.getId());
           log.info(Optional.ofNullable(address).map(AddressDto::getAddress1).orElse(null));

        } catch (DataNotFoundException e) {
           e.printStackTrace();
        }

    }

    @Test
    void updateAddress() {
        MemberResponseDto member =  authService.signup(
                MemberRequestDto.builder()
                        .username("sam")
                        .email("sammyyoo1324@gmail.com")
                        .password("aaa")
                        .build()
        );




        try {
            AddressDto address= addressService.updateAddress(member.getId(),     AddressDto.builder()
                    .address1("1414 S 235th pl")
                    .city("Des Moines")
                    .state("WA")
                    .zipCode("98198")
                    .build());


            log.info(Optional.ofNullable(member.getAddress()).map(AddressDto::getAddress1).orElse(null));

        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }

    }
}