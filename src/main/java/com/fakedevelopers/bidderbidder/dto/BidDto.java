package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

@Getter
public class BidDto {

  private final int index;
  private final String userNickname;
  private final long bid;

  BidDto(int index, String userNickname, long bid) {
    this.index = index;
    this.userNickname = userNickname;
    this.bid = (index < 4)? -1L : bid; // 1등부터 3등까진 응찰금액을 가린다
  }
}
