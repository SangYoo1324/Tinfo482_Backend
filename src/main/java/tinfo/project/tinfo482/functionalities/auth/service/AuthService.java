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
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberRequestDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberResponseDto;
import tinfo.project.tinfo482.functionalities.auth.dto.TokenDto;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.jwt.TokenProvider;
import tinfo.project.tinfo482.functionalities.mail.service.MailService;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder;

    private final MemberRepository memberRepository;
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
            //save new user info to DB
            Member member = requestDto.toMember(passwordEncoder);
            return MemberResponseDto.of(memberRepository.save(member));
        }
    }

    public TokenDto login(MemberRequestDto requestDto) throws Exception{

        // password needs to be the one before encoded
        // why? embedded authenticationManager will automatically encode again
        // process detail......
        // 1. authenticationManager get authenticationToken which includes username, password
        // 2. CustomUserDetailsService's loadUserByUsername laoded by authenticationManager and get the Member Entity from DB using username,
        // 3. authenticationManager will encode password of authenticationToken and compare with DB password which is already encoded.
        // so, We should not use encoded password for the authetnticationToken... AuthenticationManager will do it for us.
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication(passwordEncoder);

        log.info(authenticationToken.toString());

        //Authenticate user from SecurityContextHolder with username, password
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        Member target =memberRepository.findByUsername(requestDto.getUsername()).orElse(null);
//        if(target!=null)
//            //Send verificationEmail
//        mailService.sendAuthEmail(target.getEmail());

        log.error("Checker::::::::::::::::::::::::::::::::::::::::::::::::::::");
        // generate token once 2FA verificatoin completed
        return tokenProvider.generateTokenDto(authentication);

    }


    public MemberResponseDto simpleLogin(MemberRequestDto requestDto) throws Exception {
      Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(()->new Exception("cannot find the username"));

      if(passwordEncoder.matches(requestDto.getPassword(), member.getPassword())){
          mailService.sendAuthEmail(member.getEmail());
          return MemberResponseDto.builder().email(member.getEmail()).username(member.getUsername()).build();
      }
        throw new Exception("Password not Matching");
    }
}
