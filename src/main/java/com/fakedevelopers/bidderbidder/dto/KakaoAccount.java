package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccount {

  @JsonProperty(value = "has_email")
  private boolean hasEmail;

  @JsonProperty(value = "is_email_valid")
  private boolean isEmailValid;

  @JsonProperty(value = "is_email_verified")
  private boolean isEmailVerified;

  @Email
  private String email;

  @Override
  public String toString() {
    return "KakaoAccount{" +
        "hasEmail=" + hasEmail +
        ", isEmailValid=" + isEmailValid +
        ", isEmailVerified=" + isEmailVerified +
        ", email='" + email + '\'' +
        '}';
  }
}
