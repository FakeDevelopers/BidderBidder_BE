package com.fakedevelopers.ddangddangmarket.config;

import com.fakedevelopers.ddangddangmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity  // Spring Security 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true) // 특정 권한을 가진 유저만 접근을 허용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /* TODO := UserService : 로그인 요청시, POST된 정보와 DB의 회원정보를 비교하여 체크하는 로직 구현
        추후에 UserService가 경매자, 구매자, 관리자로 나뉠 수 있음. (이를 추상화하여 UserService로 구현)
        공통적인 Security Setting 내용을 아래에 구현
     */

    private UserService userService;

    @Autowired
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호를 암호화/복호화하는 객체 리턴
        return new BCryptPasswordEncoder();

    @Override
    public void configure(WebSecurity web) throws Exception {
        // FilterChainProxy를 생성하는 필터
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
    }

    @Override
    /* HTTP Request에 대한 보안 설정 */
    protected void configure(HttpSecurity http) throws Exception {
        // .authenticate() := 매칭된 결과 중 인증된 사용자만 접근 허용
        // .permitAll() := 매칭된 결과 중 모든 사용자 접근 허용
        http.authorizeRequests()
                .antMatchers("요청 url 경로 1").authenticated()
                .antMatchers("요청 url 경로 2").authenticated()
                .antMatchers("/**").permitAll();

        http.formLogin()
                .loginPage("기본 로그인 페이지")
                .defaultSuccessUrl("로그인 성공 시 리다이렉트할 페이지")
                .permitAll();

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("로그아웃 경로"))
                .logoutSuccessUrl("/login 가 직관적이지 않을까?")
                .invalidateHttpSession(true); // 로그아웃 후 http 세션을 해체

        // 권한이 없는 사용자가 접근하였을 때 처리하는 로직 정의
        http.exceptionHandling()
                .accessDeniedPage("권한없음 관련 페이지");
    }

    @Override
    // AuthenticationManageBuilder := 사용자 인증을 담당하는 객체
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
}
