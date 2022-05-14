package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductListDto {

    private final long boardId;
    private final String thumbnail;
    private final String boardTitle;
    private final Long hopePrice;
    private final long openingBid;
    private final long tick;
    private final String ExpirationDate;
    private final int bidderCount;

}
