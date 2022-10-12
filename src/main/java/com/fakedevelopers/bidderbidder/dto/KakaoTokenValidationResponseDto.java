package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoTokenValidationResponseDto {

  @NotNull
  private Long id;

  private Integer expiresIn;
  private Integer appId;

  private Integer code; // 에러 코드에 해당

  @Override
  public String toString() {
    return "KakaoTokenValidationResponseDto{" + "id=" + id + ", expiresIn=" + expiresIn + ", appId="
        + appId + ", code=" + code + '}';
  }
}
