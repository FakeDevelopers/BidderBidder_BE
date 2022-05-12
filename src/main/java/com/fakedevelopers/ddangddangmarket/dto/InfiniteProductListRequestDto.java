package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class InfiniteProductListRequestDto {

    private final String searchWord;

    private final Integer startNumber;

    private final Integer listCount;

    InfiniteProductListRequestDto(String searchWord, Integer startNumber, Integer listCount) {
        this.searchWord = searchWord;
        this.startNumber = Objects.requireNonNullElse(startNumber, -1);
        this.listCount = Objects.requireNonNullElse(listCount, 10);
    }
}
