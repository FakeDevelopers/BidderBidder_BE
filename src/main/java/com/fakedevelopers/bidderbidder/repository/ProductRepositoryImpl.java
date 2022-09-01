package com.fakedevelopers.bidderbidder.repository;

import static com.fakedevelopers.bidderbidder.domain.Constants.SEARCH_CONTENT;
import static com.fakedevelopers.bidderbidder.domain.Constants.SEARCH_TITLE;
import static com.fakedevelopers.bidderbidder.domain.Constants.SEARCH_TITLE_AND_CONTENT;
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

    private static final QProductEntity productEntity = QProductEntity.productEntity;
    private final JPAQueryFactory jpaQueryFactory;
    private final RedisRepository redisRepository;

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
                        startNumberEqualCheck(startNumber),
                        categoryEqualCheck(productListRequestDto.getCategory()),
                        containSearchWord(
                                productListRequestDto.getSearchWord(),
                                productListRequestDto.getSearchType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(new OrderSpecifier(Order.DESC, productEntity.productId))
                .fetch();
    }

    private BooleanExpression startNumberEqualCheck(Long startNumber) {
        return startNumber == null ? null : productEntity.productId.lt(startNumber);
    }

    private BooleanExpression categoryEqualCheck(Long category) {
        return category == 0 ? null : productEntity.category.categoryId.eq(category);
    }

    private BooleanExpression containSearchWord(String searchWord, int searchType) {
        if (searchWord == null || searchWord.trim().equals("")) {
            return null;
        }
        String noSpaceWord = searchWord.replace(" ", "");
        redisRepository.saveSearchWord(searchWord);
        switch (searchType) {
            case SEARCH_TITLE:
                return productEntity.productTitle.containsIgnoreCase(noSpaceWord);
            case SEARCH_CONTENT:
                return productEntity.productContent.containsIgnoreCase(noSpaceWord);
            case SEARCH_TITLE_AND_CONTENT:
                return productEntity
                        .productTitle
                        .containsIgnoreCase(noSpaceWord)
                        .or(productEntity.productContent.containsIgnoreCase(noSpaceWord));
            default:
                throw new InvalidSearchTypeException("잘못된 검색 타입입니다.");
        }
    }
}
