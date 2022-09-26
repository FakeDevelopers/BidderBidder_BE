package com.fakedevelopers.bidderbidder.message.response;

import com.fakedevelopers.bidderbidder.model.UserEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;


/**
 * The type User info.
 */
@Data
public class UserInfo {

  // Controller가 반환하는 정보, front에서 받아서 유저 정보 표시
  @NotBlank
  private String username;

  @NotBlank
  private String email;
  @NotNull
  private String nickname;

  public UserInfo(String email, String nickname) {
    this.email = email;
    this.nickname = nickname;
  }

  public UserInfo(UserEntity entity) {
    this.email = entity.getEmail();
    this.nickname = entity.getNickname();
  }
}
