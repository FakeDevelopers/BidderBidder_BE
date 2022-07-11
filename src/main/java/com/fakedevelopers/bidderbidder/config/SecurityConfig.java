package com.fakedevelopers.bidderbidder.config;

import com.fakedevelopers.bidderbidder.filter.FirebaseTokenFilter;
import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private FirebaseAuth firebaseAuth; // Firebase 토큰 정보
    private UserDetailsService userDetailsService;

    /*
        Firebase token을 인증하는 커스텀 필터 적용
        Consists of) OAuth2UserService, firebaseAuth
          OAuth2UserService -> 인증 성공 시, 인증된 사용자 정보 얻기 위함
          firebaseAuth -> 이것을 이용하여, token 검증
     */

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 1. static resource에 대한 Filter 적용 X
        // 2. 로그인, 회원가입에 대한 Filter 적용 X
        web.ignoring()
                .antMatchers("/resources/static/**")
                .antMatchers("/user/**")
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/product/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new FirebaseTokenFilter(userDetailsService, firebaseAuth), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
