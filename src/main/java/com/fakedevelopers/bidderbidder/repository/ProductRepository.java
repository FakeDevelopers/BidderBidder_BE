package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.ProductEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  // 페이지네이션 모든 productEntity 찾기
  List<ProductEntity> findAllBy(Pageable pageable);

  // 무한스크롤 모든 productEntity 찾기
  List<ProductEntity> findAllByProductIdIsLessThanOrderByProductIdDesc(
      long productId, Pageable pageable);

  // 무한스크롤 productTitle 검색
  List<ProductEntity>
      findAllByProductTitleContainingIgnoreCaseAndProductIdIsLessThanOrderByProductIdDesc(
          String searchWord, long productId, Pageable pageable);

  // 무한스크롤 productContent 검색
  List<ProductEntity>
      findAllByProductContentContainingIgnoreCaseAndProductIdIsLessThanOrderByProductIdDesc(
          String searchWord, long productId, Pageable pageable);

  // 무한스크롤 productTitle, productContent 검색
  @Query(
      "select p from ProductEntity p where (p.productTitle like %:searchWord% or p.productContent like %:searchWord%) and p.productId < :productId order by p.productId desc")
  List<ProductEntity> searchProductByTitleAndContentInInfiniteScroll(
      String searchWord, long productId, Pageable pageable);

  // 페이지네이션 productTitle 검색
  List<ProductEntity> findAllByProductTitleContainingIgnoreCase(
      String searchWord, Pageable pageable);

  // 페이지네이션 productContent 검색
  List<ProductEntity> findAllByProductContentContainingIgnoreCase(
      String searchWord, Pageable pageable);

  // 페이지네이션 productTitle, productContent 검색
  List<ProductEntity> findAllByProductTitleContainingIgnoreCaseOrProductContentContainingIgnoreCase(
      String title, String content, Pageable pageable);

  // 페이지네이션 productTitle 검색 결과 개수
  long countAllByProductTitleContainingIgnoreCase(String searchWord);

  // 페이지네이션 productContent 검색 결과 개수
  long countAllByProductContentContainingIgnoreCase(String searchWord);

  // 페이지네이션 productTitle, productContent 검색 결과 개수
  long countAllByProductTitleContainingIgnoreCaseOrProductContentContainingIgnoreCase(
      String title, String content);

  // productId로 productEntity 찾기
  ProductEntity findByProductId(long productId);

  // 가장 최신 productEntity
  ProductEntity findTopByOrderByProductIdDesc();

  // 페이지네이션 category 검색
  @Query("select p from ProductEntity p where p.category.categoryId = :category")
  List<ProductEntity> findAllByCategory(long category, Pageable pageable);

  // 무한스크롤 category 검색 결과
  @Query(
      "select p from ProductEntity p where p.category.categoryId = :category and p.productId < :productId order by p.productId desc")
  List<ProductEntity> findAllByCategoryAndProductIdIsLessThanOrderByProductIdDesc(
      long category, long productId, Pageable pageable);

  // 페이지네이션 카테고리, productTitle, productContent 검색
  @Query(
      "select p from ProductEntity p where (p.productTitle like %:searchWord% or p.productContent like %:searchWord% ) and p.category.categoryId = :category")
  List<ProductEntity> findCateProductTitleAndContent(
      long category, String searchWord, Pageable pageable);

  // 페이지네이션 카테고리, productTitle 검색
  @Query(
      "select p from ProductEntity p where p.productTitle like %:searchWord% and p.category.categoryId = :category")
  List<ProductEntity> findCateProductTitle(long category, String searchWord, Pageable pageable);

  // 페이지네이션 카테고리, productContent 검색
  @Query(
      "select p from ProductEntity p where p.productContent like %:searchWord% and p.category.categoryId = :category")
  List<ProductEntity> findCateProductContent(long category, String searchWord, Pageable pageable);

  // 무한스크롤 카테고리, productTitle 검색
  @Query(
      "select p from ProductEntity p where (p.category.categoryId = :category and p.productTitle like %:searchWord%) and p.productId < :productId order by p.productId desc")
  List<ProductEntity> searchProductByCategoryAndTitleInInfiniteScroll(
      long category, String searchWord, long productId, Pageable pageable);

  // 무한스크롤 카테고리, productContent 검색
  @Query(
      "select p from ProductEntity p where (p.category.categoryId = :category and p.productContent like %:searchWord%) and p.productId < :productId order by p.productId desc")
  List<ProductEntity> searchProductByCategoryAndContentInInfiniteScroll(
      long category, String searchWord, long productId, Pageable pageable);

  // 무한스크롤 카테고리, productTitle, productContent 검색
  @Query(
      "select p from ProductEntity p where p.category.categoryId = :category and (p.productTitle like %:searchWord% or p.productContent like %:searchWord%) and p.productId < :productId order by p.productId desc")
  List<ProductEntity> searchProductByCategoryAndTitleAndContentInInfiniteScroll(
      long category, String searchWord, long productId, Pageable pageable);
}
