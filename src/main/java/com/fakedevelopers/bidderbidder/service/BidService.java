package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.exception.AlreadyExpiredException;
import com.fakedevelopers.bidderbidder.exception.InvalidBidException;
import com.fakedevelopers.bidderbidder.exception.ProductNotFoundException;
import com.fakedevelopers.bidderbidder.exception.UserNotFoundException;
import com.fakedevelopers.bidderbidder.model.BidEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.BidRepository;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final BidRepository bidRepository;

  public BidEntity addBid(long productId, long userId, long bid) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    validateBid(product, bid);

    BidEntity bidEntity = new BidEntity(user, product, bid);

    return bidRepository.save(bidEntity);
  }

  private void validateBid(ProductEntity product, long bid) {
    Long hopePrice = product.getHopePrice();
    int tick = product.getTick();
   long minimumBid = getMinimumBid(product,tick);
    if (hopePrice != null && hopePrice < bid) {
      throw new InvalidBidException("희망가(" + hopePrice + ") 이상 응찰 할수 없습니다.");
    } else if (minimumBid > bid) {
      throw new InvalidBidException("최소 금액(" + minimumBid + ") 이상을 입력해주세요.");
    } else if ((bid - product.getOpeningBid()) % tick != 0) {
      throw new InvalidBidException("호가(" + tick + ")에 맞게 응찰해주세요.");
    }else if (product.getExpirationDate().isBefore(LocalDateTime.now())) {
      throw new AlreadyExpiredException("경매가 이미 끝났습니다.");
    }
  }

  private long getMinimumBid(ProductEntity product, int tick){
    long minimumBid = product.getOpeningBid();
    List<BidEntity> bids = bidRepository.getBidsByProductId(product.getProductId());
    if (bids.size() > 3) { // 3등까지는 응찰금액이 안보이니까 보이는 4등보다는 높은 금액만 응찰 가능하게 한다.
      minimumBid = bids.get(3).getBid() + tick;
    }
    return minimumBid;
  }
}
