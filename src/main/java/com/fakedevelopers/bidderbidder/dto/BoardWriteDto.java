package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BoardWriteDto {
    @NotBlank(message = "제목에 빈칸은 입력불가입니다.")
    private final String boardTitle;

    @NotBlank(message = "내용에 빈칸은 입력불가입니다.")
    private final String boardContent;

    private final long openingBid;

    @Min(1)
    private final long tick;

    private final Long hopePrice;

    private final int representPicture;

    @NotNull
    private final int category;

    private final LocalDateTime expirationDate;

}
