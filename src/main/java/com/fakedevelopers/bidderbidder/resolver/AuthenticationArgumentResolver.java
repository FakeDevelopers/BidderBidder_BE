package com.fakedevelopers.bidderbidder.resolver;

import com.fakedevelopers.bidderbidder.model.UserEntity;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import reactor.util.annotation.NonNull;

@Component
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(UserEntity.class);
  }

  @Override
  public Object resolveArgument(@NonNull MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
