package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductInformationDto {

    private final long productId;
    private final String productTitle;
    private final String productContent;
    private final long openingBid;
    private final Long hopePrice;
    private final long tick;
    private final String expirationDate;
    private final String createdTime;
    private final int bidderCount;

}
