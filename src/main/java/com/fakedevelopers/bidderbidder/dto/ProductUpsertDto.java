package com.fakedevelopers.bidderbidder.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductUpsertDto {

  @NotBlank(message = "제목에 빈칸은 입력불가입니다.")
  @Size(max = 400, message = "제목은 최대 100자입니다.")
  private final String productTitle;

  @NotBlank(message = "내용에 빈칸은 입력불가입니다.")
  @Size(max = 4000, message = "내용은은 최대 100자입니다.")
  private final String productContent;

  @Min(value = 1, message = "시작가는 1이상이어야 합니다.")
  @NotNull(message = "경매 시작가에 빈칸은 입력불가입니다.")
  private final long openingBid;

  @Min(value = 1, message = "입찰가 단위는 1이상이어야 합니다.")
  @NotNull(message = "입찰가 단위에 빈칸은 입력불가입니다.")
  private final int tick;

  private final Long hopePrice;

  @NotNull(message = "대표 이미지에 빈칸은 입력불가입니다.")
  private final int representPicture;

  @NotNull(message = "카테고리에 빈칸은 입력불가입니다.")
  private final long category;

  @Future
  @NotNull(message = "경매 만료기간에 빈칸은 입력불가입니다.")
  private final LocalDateTime expirationDate;
}
