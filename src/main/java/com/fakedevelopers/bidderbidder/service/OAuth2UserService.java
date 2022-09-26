package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.domain.OAuthProfile;
import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
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
@RequiredArgsConstructor
@Service
public class OAuth2UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username Not Found!"));
  }

  /**
   * Load user or register user entity.
   *
   * @param token token을 이용하여, 사용자 검증 이후 기존의 등록된 사용자라면 정보를 load 하고 그렇지 않을 경우, 회원가입 후 정보를 load 한다.
   * @return the user entity
   */
  public UserEntity loadUserOrRegister(FirebaseToken token) {
    UserEntity user;
    try {
      user = (UserEntity) loadUserByUsername(OAuthProfile.GOOGLE_PREFIX + token.getUid());
    } catch (UsernameNotFoundException e) {
      OAuth2UserRegisterDto dto =
          OAuth2UserRegisterDto.builder()
              .username(OAuthProfile.GOOGLE_PREFIX + token.getUid().substring(0, 4))
              .email(token.getEmail())
              .nickname(Constants.INIT_NICKNAME)
              .build();

      user = register(dto);
    }
    return user;
  }

  /**
   * Register user entity.
   *
   * @param dto 회원가입에 필수적인 정보(email, nickname) <br> OAuth2 회원가입은 패스워드를 요구하지 않는다.
   * @return the user entity
   */
  @Transactional
  public UserEntity register(OAuth2UserRegisterDto dto) {
    UserEntity userEntity = dto.toUserEntity();

    userEntity = userRepository.save(userEntity);
    // nickname 필드의 postfix에 identifier 추가 (닉네임 중복 방지)
    Long uid = userEntity.getId();
    initNickname(userEntity, dto.getNickname() + uid);
    userRepository.save(userEntity);
    return userEntity;
  }

  public void initNickname(@NotNull UserEntity user, String nickname) {
    user.setNickname(nickname);
  }


}
