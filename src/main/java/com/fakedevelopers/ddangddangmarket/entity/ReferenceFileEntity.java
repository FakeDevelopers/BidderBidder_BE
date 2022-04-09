package com.fakedevelopers.ddangddangmarket.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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


    public ReferenceFileEntity(String path, String savedFileName, MultipartFile multipartFile) {
        filePath = path;
        this.savedFileName = savedFileName;
        realFileName = multipartFile.getOriginalFilename();
    }


}
