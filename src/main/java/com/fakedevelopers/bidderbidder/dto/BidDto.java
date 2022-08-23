package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

@Getter
public class BidDto {

  private final int index;
  private final String userNickName;
  private final String bid;

  BidDto(int index, String userNickName, String bid) {
    this.index = index;
    this.userNickName = userNickName;
    if (index < 4) {
      this.bid = "비공개";
    } else {
      this.bid = bid;
    }
  }
}
