package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<TermEntity, Long> {

  TermEntity findByName(String name);
}
