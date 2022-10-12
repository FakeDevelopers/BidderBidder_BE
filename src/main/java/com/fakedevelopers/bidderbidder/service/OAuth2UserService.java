package com.fakedevelopers.bidderbidder.service;

import static com.fakedevelopers.bidderbidder.domain.Constants.INIT_NICKNAME;
import static com.fakedevelopers.bidderbidder.domain.Constants.MAX_USERNAME_SIZE;

import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.properties.NaverClientProperties;
import com.fakedevelopers.bidderbidder.properties.OAuth2Properties;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type OAuth2 User Service.
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final OAuth2Properties oAuth2Properties;
  private final NaverClientProperties naverClientProperties;


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
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username Not Found!"));
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
      OAuth2UserRegisterDto dto = OAuth2UserRegisterDto.builder()
          .username(
              (username.substring(0, Math.min(token.getUid().length(), MAX_USERNAME_SIZE - 1))))
          .email(token.getEmail())
          .nickname(INIT_NICKNAME)
          .build();

      user = register(dto);
    }
    return user;
  }

  /**
   * <h1>Register user entity</h1>
   *
   * @param dto 회원가입에 필수적인 정보(email, nickname) <br> OAuth2 회원가입은 패스워드를 요구하지 않는다.
   * @return the user entity
   */
  @Transactional
  public UserEntity register(OAuth2UserRegisterDto dto) {

    UserEntity userEntity = dto.toUserEntity();

    userEntity = userRepository.save(userEntity);
    // nickname 필드의 postfix에 identifier 추가 (닉네임 중복 방지)
    if (dto.getNickname().startsWith(INIT_NICKNAME)) {
      initNickname(userEntity, dto.getNickname() + userEntity.getId());
    }
    initUsername(userEntity, userEntity.getUsername());
    userRepository.save(userEntity);
    return userEntity;
  }

  public static String makeUsernameWithPrefix(String prefix, String name) {
    String username = prefix + name;
    int maxLength = Math.min(username.length(), MAX_USERNAME_SIZE);
    return username.substring(0, maxLength - 1);
  }

  private void initNickname(@NotNull UserEntity user, String nickname) {
    user.setNickname(nickname);
  }

  private void initUsername(@NotNull UserEntity user, String username) {
    username = username.substring(0,
        Math.min(MAX_USERNAME_SIZE - 1, username.length()));
    user.setUsername(username);
  }

}
