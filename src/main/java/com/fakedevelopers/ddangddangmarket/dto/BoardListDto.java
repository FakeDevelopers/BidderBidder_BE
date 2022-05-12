package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardListDto {

    final long board_id;
    final String thumbnail;
    final String board_title;
    final Long hope_price;
    final long opening_bid;
    final long tick;
    final String remain_time;
    final int bidder_count;

}
