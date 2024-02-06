package tinfo.project.tinfo482.service;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.entity.Address;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.repo.AddressRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    private final EntityManager entityManager;


    public AddressDto getAddress(String username) throws DataNotFoundException {
       Member member = memberRepository.findByUsername(username).orElseThrow(()->new DataNotFoundException("member with username not found"));

        return member.getAddress() ==null ? null : member.getAddress().toAddressDto();
    }

    public AddressDto updateAddress(String username, AddressDto addressDto) throws DataNotFoundException {

        Member member = memberRepository.findByUsername(username).orElseThrow(()->new DataNotFoundException("member with username not found"));


        member.setAddress(entityManager.merge(
                Address.builder()
                        .id(Optional.ofNullable(member.getAddress()).map(Address::getId).orElse(null))
                        .address1(addressDto.getAddress1())
                        .state(addressDto.getState())
                        .city(addressDto.getCity())
                        .zipCode(addressDto.getZipCode())
                        .build()

        ));

        memberRepository.save(member);

        log.info(String.valueOf(Optional.ofNullable(member.getAddress()).map(Address::getId).orElse(null)));

//        addressRepository.save(
//                Address.builder()
//                        .id( Optional.ofNullable(member.getAddress()).map(Address::getId).orElse(null))
//                        .address1(addressDto.getAddress1())
//                        .state(addressDto.getState())
//                        .city(addressDto.getCity())
//                        .zipCode(addressDto.getZipCode())
//                        .build()
//        );

        return member.getAddress().toAddressDto();
    }

    public void detachExistingAddr(String username) throws DataNotFoundException {

        Member member = memberRepository.findByUsername(username).orElse(null);

        if(member !=null){
            member.setAddress(null);
            memberRepository.save(member);
            return;
        }

        throw new DataNotFoundException("data not found");
    }
}
