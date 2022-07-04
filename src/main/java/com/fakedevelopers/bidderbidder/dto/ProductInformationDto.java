package com.fakedevelopers.bidderbidder.dto;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductInformationDto {

    private final String productTitle;
    private final String productContent;
    private final long openingBid;
    private final Long hopePrice;
    private final long tick;
    private final String expirationDate;
    private final String createdTime;
    private final int bidderCount;
    private final List<String> images;

    public ProductInformationDto(ProductEntity productEntity, int bidderCount, List<String> images) {
        this.productTitle = productEntity.getProductTitle();
        this.productContent = productEntity.getProductContent();
        this.openingBid = productEntity.getOpeningBid();
        this.hopePrice = productEntity.getHopePrice();
        this.tick = productEntity.getTick();
        this.expirationDate = productEntity.getExpirationDate().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
        this.createdTime = productEntity.getCreatedTime().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
        this.bidderCount = bidderCount;
        this.images = images;
    }
}
