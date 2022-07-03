package com.fakedevelopers.bidderbidder.dto;

import com.fakedevelopers.bidderbidder.model.ProductEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductSearchCountDto {

    private final long itemCount;
    private final List<ProductEntity> items;

    public ProductSearchCountDto(long itemCount, List<ProductEntity> items) {
        this.itemCount = itemCount;
        this.items = items;
    }

}
