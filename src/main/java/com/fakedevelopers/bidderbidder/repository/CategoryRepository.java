package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  @Query("select c from CategoryEntity c where c.parentCateId is null")
  List<CategoryEntity> findAllLevel1();
}
