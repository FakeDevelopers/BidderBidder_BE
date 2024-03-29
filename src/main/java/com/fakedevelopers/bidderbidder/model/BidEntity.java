package com.fakedevelopers.bidderbidder.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BidEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToOne(optional = false)
  private ProductEntity product;

  @Column(nullable = false)
  private long bid;

  public BidEntity(UserEntity user, ProductEntity product, long bid) {
    this.user = user;
    this.product = product;
    this.bid = bid;
  }
}
