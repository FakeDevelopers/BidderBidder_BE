package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
    extends JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {

  // productId로 productEntity 찾기
  ProductEntity findByProductId(long productId);

  // 가장 최신 productEntity
  ProductEntity findTopByOrderByProductIdDesc();
}
