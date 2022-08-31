package com.fakedevelopers.bidderbidder.dto;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductListDto {

  private final long productId;
  private final String thumbnail;
  private final String productTitle;
  private final Long hopePrice;
  private final long openingBid;
  private final long tick;
  private final String expirationDate;
  private final int bidderCount;

  public ProductListDto(ProductEntity productEntity, boolean isWeb, Integer bidderCount) {
    productId = productEntity.getProductId();
    thumbnail =
        "/product/getThumbnail?productId=" + productEntity.getProductId() + "&isWeb=" + isWeb;
    productTitle = productEntity.getProductTitle();
    hopePrice = productEntity.getHopePrice();
    openingBid = productEntity.getOpeningBid();
    tick = productEntity.getTick();
    expirationDate =
        productEntity
            .getExpirationDate()
            .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    this.bidderCount = (bidderCount == null ? 0 : bidderCount);
  }
}
