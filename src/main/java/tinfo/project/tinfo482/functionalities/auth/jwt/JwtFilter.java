package tinfo.project.tinfo482.functionalities.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER= "Authorization";
    public static final String BEARER_PREFIX = "Bearer";
    private final TokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        // if jwt exist && token is valid
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            // set User Authenticated on the spring security server
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //Check refreshToken inside member DB valid
        else{

            //if refreshToken inside member DB valid, regenerate jwt token and authenticate the user

            // if refreshToken also not valid, just
        }
        // go to next filter chain
        filterChain.doFilter(request,response);

    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            log.info("Getting Authorization header from HttpRequest...  "+ bearerToken);
            return bearerToken.substring(7);  // except bearer
        }
        log.info("Authorization Header doesn't exist or Bearer prefix doesn't exist");
        return null;
    }
}
