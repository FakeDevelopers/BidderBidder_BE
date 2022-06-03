package com.fakedevelopers.bidderbidder.model;

import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.exception.InvalidCategoryException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
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
import javax.persistence.OneToMany;
import java.io.File;
import java.io.FileOutputStream;
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
public class ProductEntity {

    // 게시글 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    // 게시글 제목
    @Column(nullable = false, length = 300)
    private String productTitle;

    // 게시글 내용
    @Column(nullable = false, length = 3000)
    private String productContent;

    // 시작가
    @Column(nullable = false)
    private long openingBid;

    // 희망가
    private Long hopePrice;

    // 호가
    @Column(nullable = false)
    private long tick;

    // 등록시간
    @Column
    @CreatedDate
    private LocalDateTime createdTime;

    // 수정시간
    @Column
    @LastModifiedDate
    private LocalDateTime modifiedTime;

    @Column
    private int representPicture;

    // 카테고리
    @Column(nullable = false)
    private Category category;

    // 경매 만료일
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    // 유저 DB 외래키
//    @ManyToOne(targetEntity = User)
//    @JoinColumn(name = "user_id")
//    private long user_id;

    // 파일
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "productEntity")
    private List<FileEntity> fileEntities;

    public ProductEntity(String path, ProductWriteDto productWriteDto, List<MultipartFile> files) throws Exception {
        productTitle = productWriteDto.getProductTitle();
        productContent = productWriteDto.getProductContent();
        openingBid = productWriteDto.getOpeningBid();
        hopePrice = productWriteDto.getHopePrice();
        tick = productWriteDto.getTick();
        representPicture = productWriteDto.getRepresentPicture();
        category = findCategory(productWriteDto.getCategory());
        expirationDate = productWriteDto.getExpirationDate();
        fileEntities = makeFileEntityList(path, files);
    }

    // 파일 엔티티 리스트 만듦
    private List<FileEntity> makeFileEntityList(String path, List<MultipartFile> files) throws IOException {
        List<FileEntity> list = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String savedFileName = UUID.randomUUID() + "." + extension;
                list.add(new FileEntity(path, savedFileName, this, file.getOriginalFilename()));
                FileOutputStream fo = new FileOutputStream(new File(path, savedFileName));
                fo.write(file.getBytes());
                fo.close();
            }
        }
        return list;
    }

    // 입력 받은 카테고리 있는지 확인 후 반환
    private Category findCategory(int index) {
        if (Category.CATEGORY_VALUES.length > index) {
            return Category.CATEGORY_VALUES[index];
        }
        throw new InvalidCategoryException("카테고리가 없어요");
    }
}
