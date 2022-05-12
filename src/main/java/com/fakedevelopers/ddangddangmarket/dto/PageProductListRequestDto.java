package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class PageProductListRequestDto {
    private final String searchWord;

    private final Integer listCount;

    private final Integer page;

    PageProductListRequestDto(String searchWord, Integer listCount, Integer page) {
        this.searchWord = searchWord;
        this.listCount = Objects.requireNonNullElse(listCount, 10);
        this.page = Objects.requireNonNullElse(page, 1);
    }
}
