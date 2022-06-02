package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.BoardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    List<BoardEntity> findAllBy(Pageable pageable);

    List<BoardEntity> findAllByBoardIdIsLessThanOrderByBoardIdDesc(long boardId, Pageable pageable);

    List<BoardEntity> findAllByBoardTitleContainingIgnoreCaseAndBoardIdIsLessThanOrderByBoardIdDesc(String searchWord, long boardId, Pageable pageable);

    List<BoardEntity> findAllByBoardContentContainingIgnoreCaseAndBoardIdIsLessThanOrderByBoardIdDesc(String searchWord, long boardId, Pageable pageable);

    @Query("select b from BoardEntity b where (b.boardTitle like %:searchWord% or b.boardContent like %:searchWord%) and b.boardId <= :boardId order by b.boardId desc")
    List<BoardEntity> searchBoardByTitleAndContentInInfiniteScroll(String searchWord, long boardId, Pageable pageable);

    List<BoardEntity> findAllByBoardTitleContainingIgnoreCase(String searchWord, Pageable pageable);

    List<BoardEntity> findAllByBoardContentContainingIgnoreCase(String searchWord, Pageable pageable);

    List<BoardEntity> findAllByBoardTitleContainingIgnoreCaseOrBoardContentContainingIgnoreCase(String title, String content, Pageable pageable);

    BoardEntity findByBoardId(long boardId);

    BoardEntity findTopByOrderByBoardIdDesc();
}
