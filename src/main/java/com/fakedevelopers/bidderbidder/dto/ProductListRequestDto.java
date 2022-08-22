package com.fakedevelopers.bidderbidder.dto;

import java.util.Objects;
import lombok.Getter;

@Getter
public class ProductListRequestDto {

  private final String searchWord;

  private final Integer listCount;

  private final Integer searchType;

  ProductListRequestDto(String searchWord, Integer listCount, Integer searchType) {
    this.searchWord = searchWord;
    this.listCount = Objects.requireNonNullElse(listCount, 10);
    this.searchType = Objects.requireNonNullElse(searchType, 2);
  }
}
