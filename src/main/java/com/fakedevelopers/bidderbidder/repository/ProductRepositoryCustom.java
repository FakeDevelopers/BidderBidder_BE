package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

  List<ProductEntity> findAllProduct(
      Long startNumber, ProductListRequestDto productListRequestDto, Pageable pageable);
}
