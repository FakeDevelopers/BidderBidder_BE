package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ProductListRequestDto {

  private final String searchWord;

  private final Integer listCount;

  private final Integer searchType;

  private final Long category;

  ProductListRequestDto(String searchWord, Integer listCount, Integer searchType, Long category) {
    this.searchWord = searchWord;
    this.listCount = Objects.requireNonNullElse(listCount, 10);
    this.searchType = Objects.requireNonNullElse(searchType, 2);
    this.category = Objects.requireNonNullElse(category, 0L);
  }
}
