package com.fakedevelopers.bidderbidder.util;

import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpHeaders;

/**
 * Authorization: <?> 값을 가져오는 유틸리티 클래스
 * 1. "Authorization: <?>" 형태
 * 2. HttpServletRequest 형태
 */
public class RequestUtil {

  /**
   * @param header authorization: <?>의 형태로 주어지는 string
   * @return authorization token string
   */
  public static String getAuthorizationToken(String header) {
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

  public static String getAuthorizationToken(HttpServletRequest request) {
    return getAuthorizationToken(request.getHeader(HttpHeaders.AUTHORIZATION));
  }
}
