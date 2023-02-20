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
  private final String category;
  private final Long hopePrice;
  private final long openingBid;
  private final int tick;
  private final String expirationDate;
  private final int bidderCount;

  public ProductListDto(ProductEntity productEntity, boolean isWeb, int bidderCount) {
    productId = productEntity.getProductId();
    // revision은 수정했을 때, 캐싱된 썸네일이 아닌 수정된 썸네일을 위한 값
    thumbnail =
        "/product/getThumbnail?productId=" + productEntity.getProductId() + "&isWeb="
            + isWeb + "&revision=" + productEntity.getModifiedDate()
            .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REVISION));
    productTitle = productEntity.getProductTitle();
    category = productEntity.getCategory().getCategoryName();
    hopePrice = productEntity.getHopePrice();
    openingBid = productEntity.getOpeningBid();
    tick = productEntity.getTick();
    expirationDate =
        productEntity
            .getExpirationDate()
            .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    this.bidderCount = bidderCount;
  }
}
