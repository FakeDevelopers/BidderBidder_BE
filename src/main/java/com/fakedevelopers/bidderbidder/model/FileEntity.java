package com.fakedevelopers.bidderbidder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long fileId;

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
    private ProductEntity productEntity;

    public FileEntity(String filePath, String savedFileName, ProductEntity productEntity, String realFileName) {
        this.filePath = filePath;
        this.savedFileName = savedFileName;
        this.productEntity = productEntity;
        this.realFileName = realFileName;
    }
}
