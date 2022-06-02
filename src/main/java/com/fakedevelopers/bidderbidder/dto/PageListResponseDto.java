package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageListResponseDto {
    private final long itemCount;
    private final List<ProductListDto> items;

    public PageListResponseDto(long itemCount, List<ProductListDto> items) {
        this.itemCount = itemCount;
        this.items = items;
    }
}
