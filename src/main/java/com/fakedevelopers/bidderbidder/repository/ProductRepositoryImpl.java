package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.exception.InvalidSearchTypeException;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.QProductEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final RedisRepository redisRepository;
  private static final QProductEntity productEntity = QProductEntity.productEntity;

  public ProductRepositoryImpl(JPAQueryFactory jpaQueryFactory, RedisRepository redisRepository) {
    this.jpaQueryFactory = jpaQueryFactory;
    this.redisRepository = redisRepository;
  }

  @Override
  public List<ProductEntity> findAllProduct(
      Long startNumber, ProductListRequestDto productListRequestDto, Pageable pageable) {
    return jpaQueryFactory
        .selectFrom(productEntity)
        .where(
            eqStartNumber(startNumber),
            eqCategory(productListRequestDto.getCategory()),
            containSearchWord(
                productListRequestDto.getSearchWord(), productListRequestDto.getSearchType()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(new OrderSpecifier(Order.DESC, productEntity.productId))
        .fetch();
  }

  private BooleanExpression eqStartNumber(Long startNumber) {
    return startNumber == null ? null : productEntity.productId.lt(startNumber);
  }

  private BooleanExpression eqCategory(Long category) {
    return category == 0 ? null : productEntity.category.categoryId.eq(category);
  }

  private BooleanExpression containSearchWord(String searchWord, int searchType) {
    if (searchWord == null || searchWord.trim().equals("")) {
      return null;
    } else {
      String noSpaceWord = searchWord.replace(" ", "");
      redisRepository.saveSearchWord(searchWord);
      switch (searchType) {
        case 0:
          return productEntity.productTitle.containsIgnoreCase(noSpaceWord);
        case 1:
          return productEntity.productContent.containsIgnoreCase(noSpaceWord);
        case 2:
          return productEntity
              .productTitle
              .containsIgnoreCase(noSpaceWord)
              .or(productEntity.productContent.containsIgnoreCase(noSpaceWord));
        default:
          throw new InvalidSearchTypeException("잘못된 검색 타입입니다.");
      }
    }
  }
}
