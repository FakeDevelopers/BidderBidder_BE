package com.fakedevelopers.bidderbidder.service;

import static com.fakedevelopers.bidderbidder.domain.Constants.INIT_NICKNAME;
import static com.fakedevelopers.bidderbidder.domain.Constants.MAX_USERNAME_SIZE;

import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

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
