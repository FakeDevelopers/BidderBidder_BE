package com.fakedevelopers.bidderbidder.dto;

import static com.fakedevelopers.bidderbidder.domain.Constants.SEARCH_TITLE_AND_CONTENT;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductListRequestDto {

    private String searchWord;

    private Integer listCount;

    private Integer searchType;

    private Long category;

    ProductListRequestDto(String searchWord, Integer listCount, Integer searchType, Long category) {
        this.searchWord = searchWord;
        this.listCount = Objects.requireNonNullElse(listCount, 10);
        this.searchType = Objects.requireNonNullElse(searchType, SEARCH_TITLE_AND_CONTENT);
        this.category = Objects.requireNonNullElse(category, 0L);
    }
}
