package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductListDto {

  private final long productId;
  private final String thumbnail;
  private final String productTitle;
  private final Long hopePrice;
  private final long openingBid;
  private final long tick;
  private final String expirationDate;
  private final int bidderCount;

}
