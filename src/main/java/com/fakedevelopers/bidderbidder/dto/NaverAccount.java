package com.fakedevelopers.bidderbidder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverAccount {

  @NotNull
  private String id;
  @Email
  private String email;

  @Override
  public String toString() {
    return "NaverAccount{" +
        "id=" + id +
        ", email='" + email + '\'' +
        '}';
  }
}

