package com.fakedevelopers.bidderbidder.config;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// 아래는 실제 로직 작성 후 주석 해제
// @EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
        아래는 Custom Filter의 매개변수로 넘길 예정
     */
    @Autowired
    private FirebaseAuth firebaseAuth; // Firebase 토큰 정보
    @Autowired
    private UserDetailsService userDetailsService;

    /*
        Firebase token을 인증하는 커스텀 필터 적용
        Consists of) UserDetailsService, firebaseAuth
          UserDetailsService -> 인증 성공 시, 인증된 사용자 정보 얻기 위함
          firebaseAuth -> 이것을 이용하여, token 검증
     */

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 1. static resource에 대한 Filter 적용 X
        // 2. 로그인, 회원가입에 대한 Filter 적용 X
        web.ignoring()
                .antMatchers("/resources/static/**")
                .antMatchers("/user/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // .addFilterBefore(커스텀 필터 , UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
