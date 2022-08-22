package com.fakedevelopers.bidderbidder.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.exception.InvalidBidException;
import com.fakedevelopers.bidderbidder.exception.ProductNotFoundException;
import com.fakedevelopers.bidderbidder.exception.UserNotFoundException;
import com.fakedevelopers.bidderbidder.model.BidEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(args = {
    "--datasource.url=~~~",
    "--datasource.username=~~~", "--datasource.password=~~~",
    "--redis.password=~~~", "--redis.host=~~~",
    "--OAuth2.clientPassword=~~~",
    "--OAuth2.clientID=~~~"})
@Transactional // 요걸 왜 썼을지는 각자 알아보시면 좋을것 같아요!
class BidServiceTest {

  @Autowired // 테스트 할때는 어쩔수 없이 Autowired를 사용합니다
  BidService sut;

  static long productID;

  @BeforeAll
  static void createProduct(@Autowired ProductRepository productRepository) throws Exception {
    ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10, 1000000L, 0, 1,
        LocalDateTime.now().plusHours(1));
    ProductEntity product = new ProductEntity(".", productWriteDto, new ArrayList<>());
    productID = productRepository.save(product).getProductId();
  }

  @Test
  @DisplayName("이상한 유저id 값을 넣는다")
  void invalidUser() {
    long userID = -1;
    long bid = 1000000;
    assertThrows(UserNotFoundException.class, () -> sut.addBid(productID, userID, bid));
  }

  @Test
  @DisplayName("이상한 게시글id 값을 넣는다")
  void invalidProduct() {
    long productID = -1;
    long userID = 1;
    long bid = 1000000;
    assertThrows(ProductNotFoundException.class, () -> sut.addBid(productID, userID, bid));
  }

  @Test
  @DisplayName("hopePrice보다 높은 가격을 응찰한다")
  void overHopePrice() {
    long userID = 1;
    long bid = 1000000000;
    assertThrows(InvalidBidException.class, () -> sut.addBid(productID, userID, bid));
  }

  @Test
  @DisplayName("openingBid보다 낮은 가격을 응찰한다")
  void underOpeningBid() {
    long userID = 1;
    long bid = 10;
    assertThrows(InvalidBidException.class, () -> sut.addBid(productID, userID, bid));
  }

  @Test
  @DisplayName("공개된 4등의 응찰보다 낮은 응찰을 한다")
  void under4thBid() {
    long userID = 1;
    long bid = 100000;
    for (int i = 1; i < 5; i++) {
      sut.addBid(productID, i, bid);
    }

    long lowerBid = 90000;
    assertThrows(InvalidBidException.class, () -> sut.addBid(productID, userID, lowerBid));
  }

  @Test
  @DisplayName("호가 단위에 맞지않는 응찰을 한다")
  void notMathTick() {
    long userID = 1;
    long bid = 99999;
    assertThrows(InvalidBidException.class, () -> sut.addBid(productID, userID, bid));
  }

  @Test
  @DisplayName("응찰을 한다")
  void addBid() {
    long userID = 1;
    long bid = 10000;
    BidEntity bidEntity = sut.addBid(productID, userID, bid);

    assertThat(bidEntity.getId()).isGreaterThan(0);
    assertThat(bidEntity.getBid()).isEqualTo(bid);
  }
}
