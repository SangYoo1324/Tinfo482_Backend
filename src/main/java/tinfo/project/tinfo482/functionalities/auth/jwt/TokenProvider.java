package tinfo.project.tinfo482.functionalities.auth.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.functionalities.auth.dto.TokenDto;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j

public class TokenProvider {

//    payload info
    /******************
    {”sub”: “username”,

    “auth”: “ROLE_ADMIN”,

    “exp”: “date”}
    **********************/

        private static final String AUTHORITIES_KEY = "auth";
        private static final String BEARER_TYPE = "bearer";
        private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000* 60 *30;

        private final Key key;

        private final MemberRepository memberRepository;


    public TokenProvider(  @Value("${jwt.secret}")String secretKey, MemberRepository memberRepository) {
        log.info("******JWT SecretKey***********"+ secretKey);
        byte[] keyBytes  = Decoders.BASE64URL.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("key to Bytes array");
        for(byte i : keyBytes) System.out.print(i);
        log.info("key::::"+key.getEncoded());

        this.memberRepository = memberRepository;
    }



    // get fully authenticated User authentication with CustomUserDetails class extends UserDetails
    public TokenDto generateTokenDto(Authentication authentication) throws Exception{

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        Long now = new Date().getTime();
        // 30min exp time from now
        Date tokenExpiresIn = new Date(now+ACCESS_TOKEN_EXPIRE_TIME);
        log.info("Token Expires in :"+ tokenExpiresIn);


        //Actual token building process
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY,authorities)
                .setExpiration(tokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        log.info("Attributes Details:::" + authentication.getDetails());
        log.info("Attributes principal::::::::::::::::::::::::::"+authentication.getPrincipal());
        log.info("Attributes name::::::::::::::::::::::::::"+authentication.getName());
        log.info("Generated Token:::"+ accessToken);

        Member currentMember =  memberRepository.findByUsername(authentication.getName())
                .orElseThrow(()->new Exception("cannot find nickname with Authentication.getName"));

        String username = currentMember.getUsername();
        String email =  currentMember.getEmail();
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .tokenExpiresIn(tokenExpiresIn.getTime())
                .roles((List<SimpleGrantedAuthority>)authentication.getAuthorities())
                .username(username)
                .email(email)
                .build();
    }


    // get user Authenticated on the Spring server context when you get access token
    public Authentication getAuthentication(String accessToken){
        // claim will only have member role
        Claims claims = parseClaims(accessToken);
        if(claims.get(AUTHORITIES_KEY)== null){
            throw new RuntimeException("Token with No auth info...");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map((role)-> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList());

        log.info("(claims) sub::::"+ claims.getSubject());
        UserDetails principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,"", authorities);


    }


    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    // It not only parses the token but also validates the token's signature against the provided signing key
                    .parseClaimsJws(token);
            return true;
        }
        catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){ log.error("Wrong JWT Signature...");}
        catch(ExpiredJwtException e){ log.error("Expired JWT Token...");}
        catch(UnsupportedJwtException e){ log.error("Unsupported JWT Token...");}
        catch(IllegalArgumentException e){log.error("Something else other than JWT entered as argument...");}
        return false;

    }


    private Claims parseClaims(String accessToken){

        try{
            log.info("Parsed AccessToken's Claim::::");
            log.info(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().toString());
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

        }catch(ExpiredJwtException e){
            // will have just validation fail claim
            return e.getClaims();
        }


    }



    //prob merge with validateTokenMethod
    private void validateRefreshToken(){

    }


}


