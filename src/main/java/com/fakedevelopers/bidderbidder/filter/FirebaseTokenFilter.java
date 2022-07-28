package com.fakedevelopers.bidderbidder.filter;

import com.fakedevelopers.bidderbidder.util.RequestUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
=======
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
>>>>>>> e9d93bf (feat: Google OAuth2 로그인 구현)
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
@RequiredArgsConstructor
public class FirebaseTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final FirebaseAuth firebaseAuth;

<<<<<<< HEAD

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Request에 포함된 token을 저장
        FirebaseToken decodedToken;
=======
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // Request에 포함된 token을 저장
    FirebaseToken decodedToken;
>>>>>>> e9d93bf (feat: Google OAuth2 로그인 구현)

        // Authorization: Bearer ${token} 라는 Header를 포함하는 요청에 대한 필터
        // https://www.ssemi.net/what-is-the-bearer-authentication/

<<<<<<< HEAD
        try {
            String header = RequestUtil.getAuthorizationToken(request);
            decodedToken = firebaseAuth.verifyIdToken(header);
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            setUnauthorizedResponseMessage(response, "INVALID_TOKEN");
            return;
        }

        // 인증 성공시 Spring Security Context에 저장
        try {
            // 자원을 요청하는 User의 {고유 ID}를 통해 userDetails를 가져온다.
            UserDetails userDetails = userDetailsService.loadUserByUsername(decodedToken.getUid());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // 최종 인증 결과를 Security Context에 저장, 이후에는 인증 객체를 전역적으로 사용 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (NoSuchElementException e) {
            setUnauthorizedResponseMessage(response, "USER_NOT_FOUND");
            return;
        }
        // 이 후, 수행할 Filter Chain 지정
        filterChain.doFilter(request, response);
=======
    try {
      String header = RequestUtil.getAuthorizationToken(request);
      decodedToken = firebaseAuth.verifyIdToken(header);
    } catch (FirebaseAuthException | IllegalArgumentException e) {
      setUnauthorizedResponseMessage(response, "[Filter]: INVALID_TOKEN");
      return;
    }

    // 인증 성공시 Spring Security Context에 저장
    try {
      // 자원을 요청하는 User의 {고유 ID}를 통해 userDetails를 가져온다.
      UserDetails userDetails = userDetailsService.loadUserByUsername(decodedToken.getUid());
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      // 최종 인증 결과를 Security Context에 저장, 이후에는 인증 객체를 전역적으로 사용 가능
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (NoSuchElementException e) {
      setUnauthorizedResponseMessage(response, "USER_NOT_FOUND");
      return;
>>>>>>> e9d93bf (feat: Google OAuth2 로그인 구현)
    }

    private void setUnauthorizedResponseMessage(HttpServletResponse responseMessage,
                                                String typeOfError) throws IOException {
        responseMessage.setStatus(HttpStatus.SC_UNAUTHORIZED);
        responseMessage.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // client에게 반환할 출력 스트림에 error type 표시
        PrintWriter writer = responseMessage.getWriter();
        writer.write("{error: \"" + typeOfError + "\"}");
    }
}
