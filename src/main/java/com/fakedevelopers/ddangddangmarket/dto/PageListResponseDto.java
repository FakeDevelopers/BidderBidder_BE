package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;

import java.util.*;

@Getter
public class PageListResponseDto {
    private final int itemCount;
    private final List<ProductListDto> items;

    public PageListResponseDto(int maxPage, List<ProductListDto> items){
        this.itemCount = maxPage;
        this.items = items;
    }
}
