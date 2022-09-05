package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  List<CategoryEntity> findAllByParentCategoryIdIsNull();

  @Query(
      nativeQuery = true,
      value =
          "with recursive cte as (SELECT category_id, parent_category_id, 1 as level FROM category_entity "
              + "where category_id = :category UNION ALL SELECT c.category_id, c.parent_category_id, 1+level as level "
              + "from cte INNER JOIN category_entity as c ON cte.category_id = c.parent_category_id) SELECT category_id FROM cte")
  List<Long> findAllSubCategoryId(long category);
}
