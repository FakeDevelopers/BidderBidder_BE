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
  @Id
  private long categoryId;

  // 카테고리 이름
  @Column(nullable = false)
  private String categoryName;

  // 상위 카테고리 번호
  private Long parentCategoryId;

  @Column
  @OneToMany(mappedBy = "parentCategoryId")
  private List<CategoryEntity> subCategories = new ArrayList<>();

  public CategoryEntity(long categoryId, String categoryName, Long parentCategoryId) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.parentCategoryId = parentCategoryId;
  }
}
