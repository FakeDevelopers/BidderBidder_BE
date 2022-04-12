package com.fakedevelopers.ddangddangmarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FileEntity {

    // 파일 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long file_id;

    // 파일 경로
    @Column(nullable = false)
    private String filePath;

    // 파일명
    @Column(nullable = false)
    private String realFileName;

    // 저장할 파일명
    @Column(nullable = false)
    private String savedFileName;

    @ManyToOne
    @JsonIgnore
    private BoardEntity boardEntity;

    public FileEntity(String filePath, String savedFileName, BoardEntity boardEntity, MultipartFile multipartFile) {
        this.filePath = filePath;
        this.savedFileName = savedFileName;
        this.boardEntity = boardEntity;
        realFileName = multipartFile.getOriginalFilename();
    }
}
