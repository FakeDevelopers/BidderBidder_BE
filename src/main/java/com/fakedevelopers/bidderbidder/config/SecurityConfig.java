package com.fakedevelopers.bidderbidder.config;

import com.fakedevelopers.bidderbidder.filter.ExceptionHandlerFilter;
import com.fakedevelopers.bidderbidder.filter.FirebaseTokenFilter;
import com.fakedevelopers.bidderbidder.service.CustomUserDetailsService;
import com.google.firebase.auth.FirebaseAuth;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Framework 설정 파일 1. Web Security 설정 2. Http Security 설정, 필터링 관련 규칙들을 여기에 정의
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final FirebaseAuth firebaseAuth; // Firebase 토큰 정보
  private final CustomUserDetailsService customUserDetailsService;
  /*
   *  Firebase token을 인증하는 커스텀 필터 적용
   *  Consists of) OAuth2UserService, firebaseAuth
   *  OAuth2UserService -> 인증 성공 시, 인증된 사용자 정보 얻기 위함
   *  firebaseAuth -> 이것을 이용하여, token 검증
   */

  @Override
  public void configure(WebSecurity web) throws Exception {
    // 1. static resource에 대한 Filter 적용 X
    // 2. 로그인, 회원가입에 대한 Filter 적용 X
    web.ignoring()
        .antMatchers("/resources/static/**")
        .antMatchers("/user/**")
        .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
            "/term/**")
        .regexMatchers(".product.(?!write).*");

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/product/write/**").authenticated()
        .anyRequest().permitAll() // 현재 모든 인증은 수행되지 않는다.
        .and()
        .addFilterBefore(new FirebaseTokenFilter(customUserDetailsService, firebaseAuth),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new ExceptionHandlerFilter(), FirebaseTokenFilter.class)
        .exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider());
  }
}
