package com.fakedevelopers.bidderbidder.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoTokenValidationResponseDto {

  @NotNull
  private Long id;
  private Integer expires_in;
  private Integer app_id;

  private Integer code; // 에러 코드에 해당

  @Override
  public String toString() {
    return "KakaoTokenValidationResponseDto{" + "id=" + id + ", expires_in=" + expires_in
        + ", app_id=" + app_id + '}';
  }
}
