package com.fakedevelopers.ddangddangmarket.repository;

import com.fakedevelopers.ddangddangmarket.model.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
}
