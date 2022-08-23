package com.fakedevelopers.bidderbidder.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.exception.AlreadyExpiredException;
import com.fakedevelopers.bidderbidder.exception.InvalidBidException;
import com.fakedevelopers.bidderbidder.exception.ProductNotFoundException;
import com.fakedevelopers.bidderbidder.exception.UserNotFoundException;
import com.fakedevelopers.bidderbidder.model.BidEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  @Autowired
  ProductRepository productRepository;

  static long productID;

  @BeforeAll
  static void createProduct(@Autowired ProductRepository productRepository) throws Exception {
    ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10, 1000000L, 0, 1,
        LocalDateTime.now().plusHours(1));
    ProductEntity product = new ProductEntity(".", productWriteDto, new ArrayList<>());
    productID = productRepository.save(product).getProductId();
  }

  private static Stream<Arguments> parameterGenerator() {
    return Stream.of(
        Arguments.of(productID, -1, 1000000, UserNotFoundException.class), // 이상한 유저정보가 넘어옴
        Arguments.of(-1, 1, 1000000, ProductNotFoundException.class), // 이상한 상품ID를 입력함
        Arguments.of(productID, 1, 1000000000, InvalidBidException.class), // 희망가를 뛰어넘는 값을 입력함
        Arguments.of(productID, 1, 10, InvalidBidException.class) // 시작가 보다 낮은 값을 입력함
    );
  }

  @ParameterizedTest
  @MethodSource("parameterGenerator")
  @DisplayName("이상한 값을 넣는다")
  void invalidValues(long productID, long userID, long bid, Class<RuntimeException> errorClass) {
    assertThrows(errorClass, () -> sut.addBid(productID, userID, bid));
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
  @DisplayName("이미 끝난 경매에 응찰을 한다.")
  void alreadyExpired() throws Exception {

    ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10, 1000000L, 0, 1,
        LocalDateTime.now().minusHours(1));
    ProductEntity product = new ProductEntity(".", productWriteDto, new ArrayList<>());

    long productID = productRepository.save(product).getProductId();
    long userID = 1;
    long bid = 100000;

    assertThrows(AlreadyExpiredException.class, () -> sut.addBid(productID, userID, bid));
  }

  @Test
  @DisplayName("응찰을 한다")
  void addBid() {
    long userID = 1;
    long bid = 10000;
    BidEntity bidEntity = sut.addBid(productID, userID, bid);

    assertThat(bidEntity.getId()).isPositive();
    assertThat(bidEntity.getBid()).isEqualTo(bid);
  }
}
