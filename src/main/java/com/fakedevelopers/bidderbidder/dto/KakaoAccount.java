package com.fakedevelopers.bidderbidder.dto;

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

  private boolean has_email;
  private boolean is_email_valid;
  private boolean is_email_verified;
  @Email
  private String email;
}
