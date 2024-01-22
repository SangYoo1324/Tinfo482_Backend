package tinfo.project.tinfo482.functionalities.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import tinfo.project.tinfo482.functionalities.auth.dto.MFAValidateDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberRequestDto;
import tinfo.project.tinfo482.functionalities.auth.dto.MemberResponseDto;
import tinfo.project.tinfo482.functionalities.auth.dto.TokenDto;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.service.AuthService;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class AuthApiController {


    private final AuthService authService;
    private final RedisUtilService redisUtilService;


    @PostMapping("/signup")
    public ResponseEntity<?> signup( @Valid @RequestBody MemberRequestDto requestDto, BindingResult bindingResult ) {
        //catches parameter input error
        if(bindingResult.hasErrors()){
            Map<String, String> errs = new HashMap<>();
            for(FieldError err: bindingResult.getFieldErrors()){
                errs.put(err.getField(),err.getDefaultMessage());
            }
            return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST.value());
        }


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

    @PostMapping("/oauth/login")
    public ResponseEntity<MemberResponseDto> auth2_login(@RequestBody MemberRequestDto requestDto)  {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.oAuthLogin(requestDto));
        } catch (Exception e) {
            e.printStackTrace();
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

        log.info("email:::"+email);
        log.info("password:::"+ password);
        log.info("username:::"+username);


        if(email.equals(stored_verification_email)){
            try {
                if(password == "OAuth"){
                    log.info("OAuth related");
                    return ResponseEntity.status(HttpStatus.OK).body(authService.oAuthValidation(MemberRequestDto.builder()
                            .password(null)
                            .email(email)
                            .username(username)
                            .build()));
                }

               return ResponseEntity.status(HttpStatus.OK).body(authService.validation(MemberRequestDto.builder()
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
