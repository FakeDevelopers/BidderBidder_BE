package com.fakedevelopers.ddangddangmarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "reference_file_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String realFileName;

    @Column(nullable = false)
    private String savedFileName;

    @ManyToOne
    @JsonIgnore
    private ReferenceEntity referenceEntity;

    public ReferenceFileEntity(String path, String savedFileName,ReferenceEntity referenceEntity, MultipartFile multipartFile) {
        filePath = path;
        this.savedFileName = savedFileName;
        this.referenceEntity = referenceEntity;
        realFileName = multipartFile.getOriginalFilename();
    }
}
