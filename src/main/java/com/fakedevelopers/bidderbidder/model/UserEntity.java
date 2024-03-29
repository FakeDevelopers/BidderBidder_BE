package com.fakedevelopers.bidderbidder.model;

import static com.fakedevelopers.bidderbidder.domain.Constants.MAX_USERNAME_SIZE;
import static com.fakedevelopers.bidderbidder.domain.Constants.MIN_USERNAME_SIZE;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The type User entity.
 * <p>
 * 패스워드가 null 일 경우, OAuth2 로그인 방식으로 접속한 것. <br> 내부 회원가입은 반드시 not-null 이어야한다.
 * </p>
 */
@NoArgsConstructor
@Data
@Entity
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(unique = true, nullable = false)
  @Length(min = MIN_USERNAME_SIZE, max = MAX_USERNAME_SIZE)
  private String username; // 유저를 고유하게 구분할 수 있는 String을 의미합니다.

  @Email(message = "이메일의 형식을 따라야 합니다.")
  @Column(unique = true)
  private String email;

  @Column(nullable = false)
  @NotBlank(message = "형식이 잘못되었습니다.")
  @Length(min = 3, max = 12)
  private String nickname;

  @Column
  private String password;

  /**
   * Instantiates a new User entity. *
   *
   * @param username 아이디는 6~12글자의 (영문자, 숫자, _)만 사용이 가능
   * @param email    이메일 형식 준수, nullable
   * @param nickname 기본값(Constants.java) 참고
   * @param password null일 경우 OAuth 로그인
   */
  @Builder
  public UserEntity(String username, String email, String nickname, String password) {
    this.username = username;
    this.email = email;
    this.nickname = nickname;
    this.password = password;
  }

  /* 아래는 firebase와 관련된 내용 */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    // User Identifier
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
