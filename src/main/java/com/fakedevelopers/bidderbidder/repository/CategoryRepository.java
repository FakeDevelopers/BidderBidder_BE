package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  List<CategoryEntity> findAllByParentCategoryIdIsNull();
}
