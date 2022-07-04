package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllBy(Pageable pageable);

    List<ProductEntity> findAllByProductIdIsLessThanOrderByProductIdDesc(long productId, Pageable pageable);

    List<ProductEntity> findAllByProductTitleContainingIgnoreCaseAndProductIdIsLessThanOrderByProductIdDesc(String searchWord, long productId, Pageable pageable);

    List<ProductEntity> findAllByProductContentContainingIgnoreCaseAndProductIdIsLessThanOrderByProductIdDesc(String searchWord, long productId, Pageable pageable);

    @Query("select p from ProductEntity p where (p.productTitle like %:searchWord% or p.productContent like %:searchWord%) and p.productId < :productId order by p.productId desc")
    List<ProductEntity> searchProductByTitleAndContentInInfiniteScroll(String searchWord, long productId, Pageable pageable);

    List<ProductEntity> findAllByProductTitleContainingIgnoreCase(String searchWord, Pageable pageable);

    List<ProductEntity> findAllByProductContentContainingIgnoreCase(String searchWord, Pageable pageable);

    List<ProductEntity> findAllByProductTitleContainingIgnoreCaseOrProductContentContainingIgnoreCase(String title, String content, Pageable pageable);

    long countAllByProductTitleContainingIgnoreCase(String searchWord);

    long countAllByProductContentContainingIgnoreCase(String searchWord);

    long countAllByProductTitleContainingIgnoreCaseOrProductContentContainingIgnoreCase(String title, String content);

    ProductEntity findByProductId(long productId);

    ProductEntity findTopByOrderByProductIdDesc();
}
