package com.fakedevelopers.bidderbidder.model;

import com.fakedevelopers.bidderbidder.dto.ProductUpsertDto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductEntity extends BaseTimeEntity {

  // 게시글 고유 번호
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long productId;

  // 게시글 제목
  @Column(nullable = false, length = 400)
  private String productTitle;

  // 게시글 내용
  @Column(nullable = false, length = 4000)
  private String productContent;

  // 시작가
  @Column(nullable = false)
  private long openingBid;

  // 희망가
  private Long hopePrice;

  // 호가
  @Column(nullable = false)
  private int tick;

  @Column
  private int representPicture;

  // 카테고리
  @ManyToOne
  private CategoryEntity category;

  // 경매 만료일
  @Column(nullable = false)
  private LocalDateTime expirationDate;

  // 유저 DB 외래키
  @ManyToOne(optional = false)
  private UserEntity user;

  // 파일
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "productEntity")
  private List<FileEntity> fileEntities;

  public ProductEntity(
      String path,
      ProductUpsertDto productUpsertDto,
      List<MultipartFile> files,
      CategoryEntity category,
      UserEntity user)
      throws IOException {
    productTitle = productUpsertDto.getProductTitle();
    productContent = productUpsertDto.getProductContent();
    openingBid = productUpsertDto.getOpeningBid();
    hopePrice = productUpsertDto.getHopePrice();
    tick = productUpsertDto.getTick();
    representPicture = productUpsertDto.getRepresentPicture();
    this.category = category;
    expirationDate = productUpsertDto.getExpirationDate();
    fileEntities = makeFileEntityList(path, files);
    this.user = user;
  }

  public void modify(
      String path,
      ProductUpsertDto productUpsertDto,
      List<MultipartFile> files,
      CategoryEntity category)
      throws IOException {
    productTitle = productUpsertDto.getProductTitle();
    productContent = productUpsertDto.getProductContent();
    openingBid = productUpsertDto.getOpeningBid();
    hopePrice = productUpsertDto.getHopePrice();
    tick = productUpsertDto.getTick();
    representPicture = productUpsertDto.getRepresentPicture();
    this.category = category;
    expirationDate = productUpsertDto.getExpirationDate();
    fileEntities = makeFileEntityList(path, files);
  }

  // 파일 엔티티 리스트 만듦
  private List<FileEntity> makeFileEntityList(String path, List<MultipartFile> files)
      throws IOException {
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
}
