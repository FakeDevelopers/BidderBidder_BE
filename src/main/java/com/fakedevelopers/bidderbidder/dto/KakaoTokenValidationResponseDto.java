package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty(value = "expires_in")
  private Integer expiresIn;
  @JsonProperty(value = "app_id")
  private Integer appId;

  private Integer code; // 에러 코드에 해당

  @Override
  public String toString() {
    return "KakaoTokenValidationResponseDto{" + "id=" + id + ", expiresIn=" + expiresIn + ", appId="
        + appId + ", code=" + code + '}';
  }
}
