package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
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
    return userRepository.findByEmail(username)
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
      user = (UserEntity) loadUserByUsername(token.getEmail());
    } catch (UsernameNotFoundException e) {
      OAuth2UserRegisterDto dto = OAuth2UserRegisterDto.builder().email(token.getEmail())
          .nickname(Constants.INIT_NICKNAME).build();
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
    UserEntity userEntity = UserEntity.builder().email(dto.getEmail()).build();
    userEntity = userRepository.save(userEntity);
    // nickname 필드의 postfix에 identifier 추가 (닉네임 중복 방지)
    // UID가 존재하지 않는 경우, IllegalAccessError 예외(사용자가 이전에 로그인한 구글 계정이 더 이상 존재하지 않을 경우)
    Long uid = userEntity.getId();
    initNickname(userEntity, dto.getNickname() + uid);
    userRepository.save(userEntity);
    return userEntity;
  }

  @Modifying
  public void initNickname(@NotNull UserEntity user, String nickname) {
    user.setNickname(nickname);
  }

}
