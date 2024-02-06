package tinfo.project.tinfo482.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.entity.Address;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.repo.AddressRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;


    public AddressDto getAddress(String username) throws DataNotFoundException {
       Member member = memberRepository.findByUsername(username).orElseThrow(()->new DataNotFoundException("member with username not found"));

        return member.getAddress() ==null ? null : member.getAddress().toAddressDto();
    }

    public AddressDto updateAddress(String username, AddressDto addressDto) throws DataNotFoundException {

        Member member = memberRepository.findByUsername(username).orElseThrow(()->new DataNotFoundException("member with username not found"));


        member.setAddress(Address.builder()
                        .address1(addressDto.getAddress1())
                        .state(addressDto.getState())
                        .city(addressDto.getCity())
                        .zipCode(addressDto.getZipCode())
                .build());

        memberRepository.save(member);

        return member.getAddress().toAddressDto();
    }
}
