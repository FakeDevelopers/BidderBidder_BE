package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.BidEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<BidEntity, Long> {

  @Query("select b from BidEntity b where b.product.productId = :productId and (b.user, b.bid) in (select b2.user, max(b2.bid) from BidEntity b2 where b2.product.productId = :productId group by b2.user) order by b.bid desc")
  List<BidEntity> getBidsByProductId(long productId);

}
