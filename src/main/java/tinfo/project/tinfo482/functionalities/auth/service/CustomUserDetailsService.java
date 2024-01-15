package tinfo.project.tinfo482.functionalities.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.functionalities.auth.entity.CustomUserDetails;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService::"+ username);
        return memberRepository.findByUsername(username).map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find "+username +" from DB..."));
    }

    private UserDetails createUserDetails(Member member){
        log.info("member Entity::::"+ member);
        return new CustomUserDetails(member);
    }
}
