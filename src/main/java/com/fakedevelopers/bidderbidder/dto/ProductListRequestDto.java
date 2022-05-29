package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ProductListRequestDto {

    private final String searchWord;

    private final Integer listCount;

    ProductListRequestDto(String searchWord, Integer listCount){
        this.searchWord = searchWord;
        this.listCount = Objects.requireNonNullElse(listCount, 10);
    }
}
