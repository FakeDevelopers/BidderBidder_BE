package com.fakedevelopers.bidderbidder.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class CategoryEntity {

  // 카테고리 번호
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long categoryId;

  // 카테고리 이름
  @Column(nullable = false)
  private String categoryName;

  // 상위 카테고리 번호
  @Column private Long parentCategoryId;

  @Column
  @OneToMany(mappedBy = "parentCategoryId")
  private List<CategoryEntity> subCategories = new ArrayList<>();

  @OneToMany
  private List<ProductEntity> productEntities;

  public CategoryEntity(String categoryName, Long parentCategoryId) {
    this.categoryName = categoryName;
    this.parentCategoryId = parentCategoryId;
  }
}
