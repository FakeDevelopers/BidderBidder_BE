package com.fakedevelopers.bidderbidder.model;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The type User entity.
 * <p>
 *   패스워드가 null 일 경우, OAuth2 로그인 방식으로 접속한 것. <br>
 *   내부 회원가입은 반드시 not-null 이어야한다.
 * </p>
 */
@NoArgsConstructor
@Data
@Entity
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Email
  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = true)
  private String password;

  /**
   * Instantiates a new User entity.
   *
   * @param email    이메일 형식 준수, not-null
   * @param nickname 기본값(Constants.java) 참고
   * @param password null일 경우 OAuth 로그인
   */
  @Builder
  public UserEntity(String email, String nickname, String password) {
    this.email = email;
    this.nickname = nickname;
    this.password = password;
  }

  /* 아래는 firebase와 관련된 내용 */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    // User Identifier
    return this.email;
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
