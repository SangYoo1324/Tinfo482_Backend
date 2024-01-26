package tinfo.project.tinfo482.functionalities.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tinfo.project.tinfo482.entity.Address;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberRequestDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberResponseDto;
import tinfo.project.tinfo482.functionalities.auth.dto.TokenDto;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.jwt.TokenProvider;
import tinfo.project.tinfo482.functionalities.mail.service.MailService;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;
import tinfo.project.tinfo482.repo.AddressRepository;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder;

    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MailService mailService;

    private final RedisUtilService redisUtilService;

    public MemberResponseDto signup(MemberRequestDto requestDto){
        // already registered user handler
        if(memberRepository.existsByEmail(requestDto.getEmail())){
            log.error("Already Registered User");
            return MemberResponseDto.of(memberRepository.findByEmail(requestDto.getEmail()).orElse(null));
        }else{
            log.info("signup function:::::");

            //if logged in with address =
            if(requestDto.getAddress() !=null){
                Address userAddress =addressRepository.save(Address.builder()
                        .address1(requestDto.getAddress().getAddress1())
                        .state(requestDto.getAddress().getState())
                        .city(requestDto.getAddress().getCity())
                        .zipCode(requestDto.getAddress().getZipCode())
                        .build());
                //save new user info to DB
                Member member = requestDto.toMember(passwordEncoder, userAddress);
                return MemberResponseDto.of(memberRepository.save(member));
            }else{
                ///if logged in without address =
                log.info("Oauth Login address blank::::");
                //Oauth Login address blank::::
                Member member = requestDto.toMember(passwordEncoder, null);
                return MemberResponseDto.of(memberRepository.save(member));
            }


        }
    }

    public TokenDto validation(MemberRequestDto requestDto) throws Exception{

        // password needs to be the one before encoded
        // why? embedded authenticationManager will automatically encode again
        // process detail......
        // 1. authenticationManager get authenticationToken which includes username, password
        // 2. CustomUserDetailsService's loadUserByUsername laoded by authenticationManager and get the Member Entity from DB using username,
        // 3. authenticationManager will encode password of authenticationToken and compare with DB password which is already encoded.
        // so, We should not use encoded password for the authetnticationToken... AuthenticationManager will do it for us.
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication(passwordEncoder);

        log.info("USernamePAsswordAuthenticationToken:::::::::"+authenticationToken.toString());

        //Authenticate user from SecurityContextHolder with username, password
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

//        Member target =memberRepository.findByUsername(requestDto.getUsername()).orElse(null);
//        if(target!=null)
//            //Send verificationEmail
//        mailService.sendAuthEmail(target.getEmail());

        log.info("isAuthenticated:::::::::"+String.valueOf(authentication.isAuthenticated()));

        log.error("Checker::::::::::::::::::::::::::::::::::::::::::::::::::::");
        // generate token once 2FA verificatoin completed
        return tokenProvider.generateTokenDto(authentication);

    }


    public MemberResponseDto simpleLogin(MemberRequestDto requestDto) throws Exception {
      Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(()->new Exception("cannot find the username"));
            log.info("requestDTO Password:::::"+requestDto.getPassword());
            log.info("Member DB Password:::::"+ member.getPassword());
            log.info(String.valueOf(passwordEncoder.matches(requestDto.getPassword(), member.getPassword())));
      if(passwordEncoder.matches(requestDto.getPassword(), member.getPassword())){
          mailService.sendAuthEmail(member.getEmail());
          // provider != null means it's social login
          if(requestDto.getProvider() !=null)
              // only socialLogin returns password(Randomly generated)
              // password should be null for the response!!!
          return MemberResponseDto.builder().email(member.getEmail()).username(member.getUsername()).provider(requestDto.getProvider()).password(null).build();


          return MemberResponseDto.builder().email(member.getEmail()).username(member.getUsername()).build();
      }
        throw new Exception("Password not Matching");
    }


    //right now, all password for oauth login set to OAuth, need to generate random password later
    public MemberResponseDto oAuthLogin(MemberRequestDto requestDto)throws Exception{
        switch (requestDto.getProvider()){
            case "GOOGLE":{
                log.info(" Google oAuthLogin:::::::::::::::");
                // generate Unique password for the user
                requestDto.setPassword("OAuth");

                Member member = memberRepository.findByEmail(requestDto.getEmail()).orElse(null);
                if(member == null){
                    log.info("Signup");
                    //signup -> Simple Login
                   signup(requestDto);
                   // signup function return the responseDto with Encoded password... We cannot use encoded password for login
                    //so, We need to create new Object
                    return simpleLogin(MemberRequestDto.builder().provider("GOOGLE").password("OAuth").email(requestDto.getEmail())
                            .username(requestDto.getUsername())
                            .build());
                }
                //if it's already signed up, just simpleLogin for email validation
                else{
                    log.info("SimpleLogin");
                    return simpleLogin(requestDto);
                }
            }

            default: throw new Exception("This is not a Social Login... No provider assigned...");
        }



    }

    public TokenDto oAuthValidation(MemberRequestDto memberRequestDto) throws Exception{
        // retrieve password via email
        Member member = memberRepository.findByEmail(memberRequestDto.getEmail()).orElseThrow(()->new Exception("Cannot find Member with provided Email..."));

        memberRequestDto.setPassword(member.getPassword());
        return validation(memberRequestDto);
    }

    public MemberResponseDto fetchUserByEmail(String email) throws Exception {
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new NoSuchElementException("Cannot find user with given username"));

        return MemberResponseDto.secureMemberResponseDto(member);
    }



}
