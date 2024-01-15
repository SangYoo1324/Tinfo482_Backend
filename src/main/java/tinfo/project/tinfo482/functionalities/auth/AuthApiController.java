package tinfo.project.tinfo482.functionalities.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tinfo.project.tinfo482.functionalities.auth.dto.MFAValidateDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberRequestDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberResponseDto;
import tinfo.project.tinfo482.functionalities.auth.dto.TokenDto;
import tinfo.project.tinfo482.functionalities.auth.service.AuthService;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class AuthApiController {


    private final AuthService authService;
    private final RedisUtilService redisUtilService;


    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto requestDto) {
        return ResponseEntity.ok(authService.signup(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody MemberRequestDto requestDto)  {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.simpleLogin(requestDto));
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @PostMapping("/mfa/validate")
    public ResponseEntity<TokenDto> validate2FA(@RequestParam("verificationCode") String verificationCode,
                                                @RequestParam("email")String email,
                                                @RequestParam("password")String password,
                                                @RequestParam("username")String username){
        String stored_verification_email = redisUtilService.getData(verificationCode);
        log.info("stored verification email::: "+ stored_verification_email);

        if(email.equals(stored_verification_email)){
            try {
               return ResponseEntity.status(HttpStatus.OK).body(authService.login(MemberRequestDto.builder()
                               .password(password)
                               .email(email)
                               .username(username)
                       .build()));

            } catch (Exception e) {
                log.error("Couldn't create the TokenDTO, failed login");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    @GetMapping("/user/test")
    public String userAuthTest(){
        return "This api requires User Authority";
    }
    @GetMapping("/admin/test")
    public String adminAuthTest(){
        return "This api requires Admin Authority";
    }

}
