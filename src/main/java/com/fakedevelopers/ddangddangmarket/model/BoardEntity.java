package com.fakedevelopers.ddangddangmarket.model;

import com.fakedevelopers.ddangddangmarket.dto.BoardWriteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class BoardEntity {

    // 게시글 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long board_id;

    // 게시글 제목
    @Column(nullable = false, length = 300)
    private String board_title;

    // 게시글 내용
    @Lob
    private String board_content;

    // 시작가
    @Column(nullable = false)
    private long opening_bid;

    // 희망가
    @Column
    private Long hope_bid;

    // 호가
    @Column(nullable = false)
    private long tick;

    // 등록시간
    @Column
    @CreatedDate
    private LocalDateTime created_time;

    // 수정시간
    @Column
    @LastModifiedDate
    private LocalDateTime modified_time;

    // 카테고리
    @Column(nullable = false)
    private Category category;

    // 경매 만료일
    @Column(nullable = false)
    private LocalDateTime end_date;

    // 유저 DB 외래키
//    @ManyToOne(targetEntity = User)
//    @JoinColumn(name = "user_id")
//    private long user_id;

    // 파일
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "boardEntity")
    private List<FileEntity> fileEntities;

    public BoardEntity(String path, BoardWriteDto boardWriteDto, List<MultipartFile> files) throws Exception {
        board_title = boardWriteDto.getBoard_title();
        board_content = boardWriteDto.getBoard_content();
        opening_bid = boardWriteDto.getOpening_bid();
        hope_bid = getHopeBid(boardWriteDto);
        tick = boardWriteDto.getTick();
        category = findCategory(boardWriteDto.getCategory());
        end_date = boardWriteDto.getEnd_date();
        fileEntities = makeFileEntityList(path, files);
    }

    // 파일 엔티티 리스트 만듦
    private List<FileEntity> makeFileEntityList(String path, List<MultipartFile> files) throws Exception {
        List<FileEntity> list = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                String savedFileName = UUID.randomUUID().toString();
                list.add(new FileEntity(path, savedFileName, this, file));
                file.transferTo(new File(path, savedFileName));
            }
        }
        return list;
    }

    // 입력 받은 카테고리 있는지 확인 후 반환
    private Category findCategory(int category) throws Exception {
        for (Category c : Category.values()) {
            if (c.ordinal() == category) {
                return c;
            }
        }
        throw new Exception("카테고리가 없어요");
    }

    // 희망가 있는지 없는지 확인 후 반환
    private Long getHopeBid(BoardWriteDto boardWriteDto) {
        Long hope;

        if (boardWriteDto.getHope_bid().isBlank()) {
            hope = null;
        } else {
            hope = Long.parseLong(boardWriteDto.getHope_bid());
        }
        return hope;
    }

}
