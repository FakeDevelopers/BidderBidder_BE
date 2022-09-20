package com.fakedevelopers.bidderbidder.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class PageListResponseDto {

  private final long itemCount;
  private final List<ProductListDto> items;

  public PageListResponseDto(long itemCount, List<ProductListDto> items) {
    this.itemCount = itemCount;
    this.items = items;
  }
}
