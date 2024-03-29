package com.fakedevelopers.bidderbidder.dto;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.model.BidEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ProductInformationDto {

  private final String productTitle;
  private final String productContent;
  private final String category;
  private final long openingBid;
  private final Long hopePrice;
  private final int tick;
  private final String expirationDate;
  private final String createdDate;
  private final int bidderCount;
  private final List<String> images;

  private final List<BidDto> bids = new ArrayList<>();

  public ProductInformationDto(ProductEntity productEntity, List<String> images,
      List<BidEntity> bidEntities) {
    this.productTitle = productEntity.getProductTitle();
    this.productContent = productEntity.getProductContent();
    this.category = productEntity.getCategory().getCategoryName();
    this.openingBid = productEntity.getOpeningBid();
    this.hopePrice = productEntity.getHopePrice();
    this.tick = productEntity.getTick();
    this.expirationDate = productEntity.getExpirationDate()
        .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    this.createdDate = productEntity.getCreatedDate()
        .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    this.bidderCount = bidEntities.size();
    this.images = images;
    for (int i = 0; i < bidderCount; i++) {
      BidEntity bid = bidEntities.get(i);
      bids.add(new BidDto(i + 1, bid.getUser().getNickname(), bid.getBid()));
    }
  }
}
