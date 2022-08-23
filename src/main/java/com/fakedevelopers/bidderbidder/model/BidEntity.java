package com.fakedevelopers.bidderbidder.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BidEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToOne(optional = false)
  private ProductEntity product;

  @Column(nullable = false)
  private long bid;

  @Column(nullable = false)
  @CreatedDate
  private LocalDateTime createdTime;

  @Column
  @LastModifiedDate
  private LocalDateTime modifiedTime;

  public BidEntity(UserEntity user, ProductEntity product, long bid) {
    this.user = user;
    this.product = product;
    this.bid = bid;
  }
}
