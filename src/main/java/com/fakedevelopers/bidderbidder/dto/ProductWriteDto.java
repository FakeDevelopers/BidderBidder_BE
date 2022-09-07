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
public class ProductWriteDto {

    @NotBlank(message = "제목에 빈칸은 입력불가입니다.")
    @Size(max = 400)
    private String productTitle;

    @NotBlank(message = "내용에 빈칸은 입력불가입니다.")
    @Size(max = 4000)
    private String productContent;

    @NotNull(message = "경매 시작가에 빈칸은 입력불가입니다.")
    private long openingBid;

    @Min(1)
    @NotNull(message = "입찰가 단위에 빈칸은 입력불가입니다.")
    private int tick;

    private Long hopePrice;

    @NotNull(message = "대표 이미지에 빈칸은 입력불가입니다. 0이라도 넣으십시요.")
    private int representPicture;

    @NotNull(message = "카테고리에 빈칸은 입력불가입니다.")
    private long category;

    @Future
    @NotNull(message = "경매 만료기간에 빈칸은 입력불가입니다.")
    private LocalDateTime expirationDate;
}
