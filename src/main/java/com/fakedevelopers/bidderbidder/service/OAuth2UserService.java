package com.fakedevelopers.bidderbidder.service;

import static com.fakedevelopers.bidderbidder.domain.Constants.INIT_NICKNAME;
import static com.fakedevelopers.bidderbidder.domain.Constants.MAX_USERNAME_SIZE;

import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.properties.NaverClientProperties;
import com.fakedevelopers.bidderbidder.properties.OAuth2Properties;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The type OAuth2 User Service.
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final OAuth2Properties oAuth2Properties;
  private final NaverClientProperties naverClientProperties;

  private final UserService userService;

  public String getBaseRedirectURI() {
    return oAuth2Properties.getRedirect().getBase().getUri();
  }

  public String getNaverClientId() {
    return naverClientProperties.getId();
  }

  public String getNaverClientSecret() {
    return naverClientProperties.getSecret();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UserEntity> result = userRepository.findByUsername(username);
    if (result.isEmpty()) {
      throw new UsernameNotFoundException("User Not Found");
    }
    return result.get();
  }

  public UserDetails loadUserByUsername(String serviceProvider, String username) {
    return loadUserByUsername(serviceProvider + username);
  }

  /**
   * <h1> Load user or register user entity. </h1>
   *
   * @param token token을 이용하여, 사용자 검증 이후 기존의 등록된 사용자라면 정보를 load 하고 그렇지 않을 경우, 회원가입 후 정보를 load 한다.
   * @return the user entity
   */
  public UserEntity loadUserOrRegister(String serviceProvider, FirebaseToken token) {
    UserEntity user;
    try {
      user = (UserEntity) loadUserByUsername(serviceProvider + token.getUid());
    } catch (UsernameNotFoundException e) {
      String username = serviceProvider + token.getUid();
      OAuth2UserRegisterDto dto = OAuth2UserRegisterDto.builder().username((username.substring(0,
              Math.min(username.length(), MAX_USERNAME_SIZE - 1))))
          .email(token.getEmail()).nickname(INIT_NICKNAME).build();

      user = register(dto);
    }
    return user;
  }

  public UserEntity register(OAuth2UserRegisterDto dto) {
    return userService.register(dto);
  }

}
