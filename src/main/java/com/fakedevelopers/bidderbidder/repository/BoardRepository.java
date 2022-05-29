package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.BoardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    List<BoardEntity> findAllBy(Pageable pageable);
    List<BoardEntity> findAllByBoardIdIsLessThanOrderByBoardIdDesc(long boardId,Pageable pageable);
    BoardEntity findTopByOrderByBoardIdDesc();
}
