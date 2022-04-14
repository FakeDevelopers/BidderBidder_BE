package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BoardWriteDto {
    @NotBlank(message = "제목에 빈칸은 입력불가입니다.")
    private final String board_title;

    @NotBlank(message = "내용에 빈칸은 입력불가입니다.")
    private final String board_content;

    private final long opening_bid;

    @Min(1)
    private final long tick;

    private final Long hope_price;

    private final int category;

    private final LocalDateTime end_date;

}
