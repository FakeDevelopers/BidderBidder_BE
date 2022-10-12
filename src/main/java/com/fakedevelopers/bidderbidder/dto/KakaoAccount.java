package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoAccount {

  private boolean hasEmail;

  private boolean isEmailValid;

  private boolean isEmailVerified;

  @Email
  private String email;

  @Override
  public String toString() {
    return "KakaoAccount{" + "hasEmail=" + hasEmail + ", isEmailValid=" + isEmailValid
        + ", isEmailVerified=" + isEmailVerified + ", email='" + email + '\'' + '}';
  }
}
