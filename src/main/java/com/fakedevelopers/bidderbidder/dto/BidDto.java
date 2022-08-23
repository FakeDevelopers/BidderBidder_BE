package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

@Getter
public class BidDto {

  private final int index;
  private final String userNickName;
  private final long bid;

  BidDto(int index, String userNickName, long bid) {
    this.index = index;
    this.userNickName = userNickName;
    this.bid = (index < 4)? -1L : bid; // 1등부터 3등까진 응찰금액을 가린다
  }
}
