package com.fakedevelopers.bidderbidder.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.dto.ProductUpsertDto;
import com.fakedevelopers.bidderbidder.exception.AlreadyExpiredException;
import com.fakedevelopers.bidderbidder.exception.InvalidBidException;
import com.fakedevelopers.bidderbidder.exception.ProductNotFoundException;
import com.fakedevelopers.bidderbidder.exception.UserNotFoundException;
import com.fakedevelopers.bidderbidder.model.BidEntity;
import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.CategoryRepository;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;


class BidServiceTest extends IntegrationTestBase {

  static long productID;
  static CategoryEntity categoryEntity; // ProductEntity 생성을 위한 객체
  static UserEntity userEntity;
  @Autowired // 테스트 할때는 어쩔수 없이 Autowired를 사용합니다
  BidService sut;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  UserRepository userRepository;

  @BeforeAll
  static void setUp(@Autowired ProductRepository productRepository,
      @Autowired CategoryRepository categoryRepository,
      @Autowired UserRepository userRepository) throws Exception {
    ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10, 1000000L, 0, 1,
        LocalDateTime.now().plusHours(1));
    userEntity = userRepository.findById(32001L).orElseThrow();
    ProductEntity product = new ProductEntity(".", productUpsertDto, new ArrayList<>(),
        categoryEntity, userEntity);
    productID = productRepository.save(product).getProductId();
    categoryEntity = categoryRepository.findAllByParentCategoryIdIsNull()
        .get(0); // ProductEntity 생성을 위해 아무 카테고리를 가져온다
  }

  @AfterAll
  static void tearDown(@Autowired ProductRepository productRepository) {
    productRepository.deleteById(productID);
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

    ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10, 1000000L, 0, 1,
        LocalDateTime.now().minusHours(1));
    ProductEntity product = new ProductEntity(".", productUpsertDto, new ArrayList<>(),
        categoryEntity, userEntity);

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
