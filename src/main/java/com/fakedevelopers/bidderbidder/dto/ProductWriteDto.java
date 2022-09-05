package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProductWriteDto {
  @NotBlank(message = "제목에 빈칸은 입력불가입니다.")
  @Size(max = 400)
  private final String productTitle;

  @NotBlank(message = "내용에 빈칸은 입력불가입니다.")
  @Size(max = 4000)
  private final String productContent;

  private final long openingBid;

  @Min(1)
  private final long tick;

  private final Long hopePrice;

  private final int representPicture;

  @NotNull private final long category;

  private final LocalDateTime expirationDate;
}
