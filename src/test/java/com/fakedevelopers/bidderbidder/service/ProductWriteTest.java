package com.fakedevelopers.bidderbidder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.dto.ProductUpsertDto;
import com.fakedevelopers.bidderbidder.exception.InvalidCategoryException;
import com.fakedevelopers.bidderbidder.exception.InvalidExpirationDateException;
import com.fakedevelopers.bidderbidder.exception.InvalidExtensionException;
import com.fakedevelopers.bidderbidder.exception.InvalidOpeningBidException;
import com.fakedevelopers.bidderbidder.exception.InvalidRepresentPictureIndexException;
import com.fakedevelopers.bidderbidder.exception.NoImageException;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("글쓰기 기능 테스트")
public class ProductWriteTest extends IntegrationTestBase {

  private static ResourceLoader testResourceLoader;
  private static List<MultipartFile> mList;
  private static UserEntity userEntity;
  @Autowired
  ProductService sut;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  UserRepository userRepository;

  private static List<MultipartFile> makeImageList(String imageName, String extension)
      throws IOException {
    InputStream inputStream =
        testResourceLoader.getResource("classpath:img/" + imageName + extension)
            .getInputStream();

    MultipartFile multipartFile = new MockMultipartFile("testImage." + extension,
        "testImage." + extension, "image/" + extension,
        inputStream);

    List<MultipartFile> imageList = new ArrayList<>();
    imageList.add(multipartFile);
    return imageList;
  }

  @BeforeAll
  static void setUp(@Autowired ResourceLoader resourceLoader,
      @Autowired UserRepository userRepository) throws IOException {
    testResourceLoader = resourceLoader;
    mList = makeImageList("Starry night.", "jpg");
    userEntity = userRepository.findById(33001L).orElseThrow();
  }

  private void deleteImage(long productId) {
    String path = "upload";
    String originalImage = productRepository.findByProductId(productId).getFileEntities().get(0)
        .getSavedFileName();
    String resizedImage =
        "resize_" + productRepository.findByProductId(productId).getProductId() + ".jpg";
    File originalFile = new File(path + File.separator + originalImage);
    File appResizedFile = new File(
        path + File.separator + "resize_app" + File.separator + resizedImage);
    File webResizedFile = new File(
        path + File.separator + "resize_web" + File.separator + resizedImage);

    originalFile.delete();
    appResizedFile.delete();
    webResizedFile.delete();
  }

  @Nested
  @DisplayName("openingBid(시작가)가")
  class Describe_openingBid {

    @Nested
    @DisplayName("hopePrice(희망가)보다 크다면")
    class Context_BiggerThanHopePrice {

      @Test
      @DisplayName("InvalidOpeningBidException를 던진다.")
      void it_throwInvalidOpeningBidException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 100000, 10,
            1000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidOpeningBidException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("representPicture(대표 이미지 번호)가")
  class Describe_representPicture {

    @Nested
    @DisplayName("0보다 작으면")
    class Context_Under0 {

      @Test
      @DisplayName("InvalidRepresentPictureIndexException를 던진다.")
      void it_throwInvalidRepresentPictureIndexException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, -1, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidRepresentPictureIndexException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }

    @Nested
    @DisplayName("이미지 개수보다 크면")
    class Context_OverImageCount {

      @Test
      @DisplayName("InvalidRepresentPictureIndexException를 던진다.")
      void it_throwInvalidRepresentPictureIndexException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 1, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidRepresentPictureIndexException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("category(카테고리 ID)가")
  class Describe_category {

    @Nested
    @DisplayName("존재하지 않으면")
    class Context_NotExistId {

      @Test
      @DisplayName("InvalidCategoryException를 던진다")
      void it_throwInvalidCategoryException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 99,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidCategoryException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }

    @Nested
    @DisplayName("최하위 카테고리가 아니면")
    class Context_NotLastSubCategory {

      @Test
      @DisplayName("InvalidCategoryException를 던진다")
      void it_throwInvalidCategoryException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 1,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidCategoryException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }

  }

  @Nested
  @DisplayName("expirationDate(기간 만료일)가")
  class Describe_expirationDate {

    @Nested
    @DisplayName("오늘보다 이전이면")
    class Context_BeforeToday {

      @Test
      @DisplayName("InvalidExpirationDateException를 던진다.")
      void it_throwInvalidExpirationDateException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().minusHours(1));
        assertThrows(InvalidExpirationDateException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }

    @Nested
    @DisplayName("72시간보다 뒤면")
    class Context_After72Hours {

      @Test
      @DisplayName("InvalidExpirationDateException를 던진다")
      void it_throwInvalidExpirationDateException() {
        // 서버에서는 시차가 존재해서 -9를 해주었지만
        // 로컬에서는 현재시간이 그대로 적용이기 때문에 72 + 9 + 1의 시간을 더해줌
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(82));
        assertThrows(InvalidExpirationDateException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("files(이미지)가")
  class Describe_files {

    @Nested
    @DisplayName("없으면")
    class Context_NotExist {

      @Test
      @DisplayName("NoImageException을 던진다.")
      void it_throwNoImageException() {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(NoImageException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, null));
      }
    }

    @Nested
    @DisplayName("확장자가 다르면")
    class Context_DifferentExtension {

      @Test
      @DisplayName("InvalidExtensionException을 던진다.")
      void it_throwInvalidExtensionException() throws IOException {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        List<MultipartFile> mList = makeImageList("Tidokang_star.", "webp");
        assertThrows(InvalidExtensionException.class,
            () -> sut.saveProduct(userEntity, productUpsertDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("글쓰기 정보를 제대로 입력했을 시")
  class Describe_productSave {

    @Nested
    @DisplayName("희망가가 없으면")
    class Context_NotExistHopePrice {

      @Test
      @DisplayName("성공한다.")
      void it_success() throws Exception {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            null, 0, 4,
            LocalDateTime.now().plusHours(1));
        ProductEntity product = sut.saveProduct(userEntity, productUpsertDto, mList);

        assertThat(product.getProductId()).isPositive();
        assertThat(product.getProductTitle()).isEqualTo(productUpsertDto.getProductTitle());
        assertThat(product.getProductContent()).isEqualTo(productUpsertDto.getProductContent());
        assertThat(product.getOpeningBid()).isEqualTo(productUpsertDto.getOpeningBid());
        assertThat(product.getTick()).isEqualTo(productUpsertDto.getTick());
        assertThat(product.getHopePrice()).isEqualTo(productUpsertDto.getHopePrice());
        assertThat(product.getRepresentPicture()).isEqualTo(productUpsertDto.getRepresentPicture());
        assertThat(product.getCategory().getCategoryId()).isEqualTo(productUpsertDto.getCategory());

        deleteImage(product.getProductId());
      }
    }

    @Nested
    @DisplayName("희망가가 있으면")
    class Context_ExistHopePrice {

      @Test
      @DisplayName("성공한다.")
      void it_success() throws Exception {
        ProductUpsertDto productUpsertDto = new ProductUpsertDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        ProductEntity product = sut.saveProduct(userEntity, productUpsertDto, mList);

        assertThat(product.getProductId()).isPositive();
        assertThat(product.getProductTitle()).isEqualTo(productUpsertDto.getProductTitle());
        assertThat(product.getProductContent()).isEqualTo(productUpsertDto.getProductContent());
        assertThat(product.getOpeningBid()).isEqualTo(productUpsertDto.getOpeningBid());
        assertThat(product.getTick()).isEqualTo(productUpsertDto.getTick());
        assertThat(product.getHopePrice()).isEqualTo(productUpsertDto.getHopePrice());
        assertThat(product.getRepresentPicture()).isEqualTo(productUpsertDto.getRepresentPicture());
        assertThat(product.getCategory().getCategoryId()).isEqualTo(productUpsertDto.getCategory());

        deleteImage(product.getProductId());
      }
    }
  }

}
