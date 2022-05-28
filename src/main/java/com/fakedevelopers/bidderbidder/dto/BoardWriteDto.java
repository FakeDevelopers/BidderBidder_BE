package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter

public class BoardWriteDto {
    @NotBlank(message = "제목에 빈칸은 입력불가입니다.")
    private final String boardTitle;

    @NotBlank(message = "내용에 빈칸은 입력불가입니다.")
    private final String boardContent;

    private final long openingBid;

    @Min(1)
    private final long tick;

    private final Long hopePrice;

    private final Integer representPicture;

    @NotNull
    private final int category;

    private final LocalDateTime expirationDate;

    BoardWriteDto(String boardTitle, String boardContent, long openingBid, long tick, Long hopePrice,
                  Integer representPicture, int category, LocalDateTime expirationDate) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.openingBid = openingBid;
        this.tick = tick;
        this.hopePrice = hopePrice;
        this.representPicture = Objects.requireNonNullElse(representPicture, 0);
        this.category = category;
        this.expirationDate = expirationDate;
    }
}
