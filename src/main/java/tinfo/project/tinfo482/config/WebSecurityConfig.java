package tinfo.project.tinfo482.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import tinfo.project.tinfo482.functionalities.auth.jwt.JwtAccessDeniedHandler;
import tinfo.project.tinfo482.functionalities.auth.jwt.JwtAuthenticationEntryPoint;
import tinfo.project.tinfo482.functionalities.auth.jwt.JwtFilter;
import tinfo.project.tinfo482.functionalities.auth.jwt.TokenProvider;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final TokenProvider tokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.
                httpBasic(httpBasic-> httpBasic.disable())

                .csrf(csrf-> csrf.disable())
                .cors(cors-> cors.disable())

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //authorizationEntryPoint is triggered when 'AuthenticationException' is thrown during
                // the authentication process(401)
                .exceptionHandling(exc->exc.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        // triggered when authenticated user tries resources have more authorization(403)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(httpRequest->httpRequest
//                        .requestMatchers("/api/login/**","/api/signup/**", String.valueOf(HttpMethod.OPTIONS)).permitAll()
//                        .requestMatchers("/api/profile/**","/api/user/**").hasRole("USER")
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()

                )
//                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)

        ;

//                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }

}
