package com.fakedevelopers.ddangddangmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
public class PageListResponseDto {
    private final int itemCount;
    private final List<ProductListDto> items;

    public PageListResponseDto(int itemCount, List<ProductListDto> items){
        this.itemCount = itemCount;
        this.items = items;
    }
}
