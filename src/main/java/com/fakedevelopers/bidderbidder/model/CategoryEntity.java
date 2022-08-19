package com.fakedevelopers.bidderbidder.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class CategoryEntity {

  // 카테고리 번호
  @Id private long cateId;

  // 카테고리 이름
  @Column(nullable = false)
  private String cateName;

  // 상위 카테고리 번호
  @Column private Long parentCateId;

  @Column
  @OneToMany(mappedBy = "parentCateId")
  private List<CategoryEntity> subCategories = new ArrayList<>();

  public CategoryEntity(long cateId, String cateName, Long parentCateId) {
    this.cateId = cateId;
    this.cateName = cateName;
    this.parentCateId = parentCateId;
  }
}
