package com.fakedevelopers.bidderbidder.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpHeaders;

/**
 * The type Request util.
 */
public class RequestUtil {
  // Request 메세지를 검증하고, 토큰 값을 가져온다.

  private RequestUtil() {
    throw new IllegalAccessError("Utility Class는 인스턴스를 생성할 수 없습니다.");
  }

  /**
   * Parse jwt token firebase token.
   *
   * @param token the token
   * @return decoded Firebase Token
   * @throws IllegalArgumentException Invalid Token Format
   * @throws FirebaseAuthException    Token is not authenticated
   */
  public static FirebaseToken parseJwtToken(String token)
      throws IllegalArgumentException, FirebaseAuthException {
    String jwtToken = getAuthorizationToken(token);
    return FirebaseAuth.getInstance().verifyIdToken(jwtToken);
  }

  /**
   * Gets authorization token.
   *
   * @param header 헤더는 Authorization: Bearer [token] 의 형식으로 구성된다.
   * @return 헤더에서 [token]을 반환한다
   * @throws IllegalArgumentException the illegal argument exception
   */
  public static String getAuthorizationToken(String header) throws IllegalArgumentException {
    // Authorization: [Bearer <token>]
    // @Param: [Bearer <token>]
    if (header == null || !header.startsWith("Bearer ")) {
      throw new IllegalArgumentException("유효하지 않은 토큰");
    }

    String[] parts = header.split(" ");
    if (parts.length != 2) {
      throw new IllegalArgumentException("유효하지 않은 토큰");
    }
    // 토큰 값을 리턴
    return parts[1];
  }

  /**
   * Gets authorization token.
   *
   * @param request the JSON request
   * @return the authorization token
   */
  public static String getAuthorizationToken(HttpServletRequest request) {
    return getAuthorizationToken(request.getHeader(HttpHeaders.AUTHORIZATION));
  }
}
