package com.fakedevelopers.ddangddangmarket.entity;

import com.fakedevelopers.ddangddangmarket.dto.ReferenceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "reference_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 200)
    private String foo;

    @Column(nullable = false)
    private int bar;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // 요거는 FetchType은 필요에 따라 변경하세요!!
    private List<ReferenceFileEntity> fileEntities;

    public ReferenceEntity(String path, ReferenceDto referenceDto) throws IOException {
        foo = referenceDto.getFoo();
        bar = referenceDto.getBar();
        fileEntities = makeFileEntityList(path, referenceDto.getFiles());
    }

    private List<ReferenceFileEntity> makeFileEntityList(String path, List<MultipartFile> files) throws IOException {
        List<ReferenceFileEntity> list = new ArrayList<>();

        for (MultipartFile file : files) {
            String savedFileName = UUID.randomUUID().toString(); // uuid가 중복이 난다는건 정말 상상도 못할일이지만 그래도 확실하게 하기 위해 중복방지로직을 생각해봐!
            list.add(new ReferenceFileEntity(path, savedFileName, file));
            file.transferTo(new File(path, savedFileName));
        }
        return list;
    }
}
