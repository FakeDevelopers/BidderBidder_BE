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
    if (index < 4) {
      this.bid = -1L;
    } else {
      this.bid = bid;
    }
  }
}
