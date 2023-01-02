package com.fakedevelopers.bidderbidder.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.util.annotation.NonNull;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  public void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (RuntimeException e) {

      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      response.setContentType(MediaType.TEXT_PLAIN_VALUE);

      PrintWriter writer = response.getWriter();
      String errorMessage = e.getMessage();
      writer.write(Objects.requireNonNullElse(errorMessage, "Internal Server Error !"));
    }
  }

}
