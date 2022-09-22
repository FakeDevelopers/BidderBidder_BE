package com.fakedevelopers.bidderbidder.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.exception.InvalidCategoryException;
import com.fakedevelopers.bidderbidder.exception.InvalidContentException;
import com.fakedevelopers.bidderbidder.exception.InvalidExpirationDateException;
import com.fakedevelopers.bidderbidder.exception.InvalidExtensionException;
import com.fakedevelopers.bidderbidder.exception.InvalidOpeningBidException;
import com.fakedevelopers.bidderbidder.exception.InvalidRepresentPictureIndexException;
import com.fakedevelopers.bidderbidder.exception.InvalidTickException;
import com.fakedevelopers.bidderbidder.exception.InvalidTitleException;
import com.fakedevelopers.bidderbidder.exception.NoImageException;
import com.fakedevelopers.bidderbidder.model.TermEntity;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.TermRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@DisplayName("글쓰기 기능 테스트")
public class ProductWriteTest extends IntegrationTestBase {

  private static List<MultipartFile> mList;
  @Autowired
  ProductService sut;
  @Autowired
  ProductRepository productRepository;

  private static List<MultipartFile> makeImageList() throws IOException {
    File file = new File("src/main/resources/static/img/Starry night.jpg");
    FileItem fileItem = new DiskFileItem("testImage", Files.probeContentType(file.toPath()),
        false, file.getName(), (int) file.length(), file.getParentFile());

    InputStream input = new FileInputStream(file);
    OutputStream output = fileItem.getOutputStream();
    IOUtils.copy(input, output);

    List<MultipartFile> imageList = new ArrayList<>();
    imageList.add(new CommonsMultipartFile(fileItem));
    return imageList;
  }

  private List<MultipartFile> makeFailImageList() throws IOException {
    File file = new File("src/main/resources/static/img/Tidokang_star.webp");
    FileItem fileItem = new DiskFileItem("testFailImage", Files.probeContentType(file.toPath()),
        false, file.getName(), (int) file.length(), file.getParentFile());

    InputStream input = new FileInputStream(file);
    OutputStream output = fileItem.getOutputStream();
    IOUtils.copy(input, output);

    List<MultipartFile> imageList = new ArrayList<>();
    imageList.add(new CommonsMultipartFile(fileItem));
    return imageList;
  }

  @BeforeAll
  static void setUp() throws IOException {
     mList = makeImageList();
  }

  @Nested
  @DisplayName("productTitle(제목)이")
  class Describe_productTitle {

    @Nested
    @DisplayName("100자가 초과하면")
    class Context_Over100Words {

      @Test
      @DisplayName("InvalidTitleException를 던진다.")
      void it_throwInvalidTitleException(){
        String str = "일이삼사오육칠팔구십";

        ProductWriteDto productWriteDto = new ProductWriteDto(str.repeat(11),
            "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidTitleException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("빈값이면")
    class Context_IsBlank {

      @Test
      @DisplayName("InvalidTitleException를 던진다.")
      void it_throwInvalidTitleException(){
        ProductWriteDto productWriteDto = new ProductWriteDto(
            "",
            "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidTitleException.class,
            () -> sut.saveProduct(productWriteDto, mList));

      }
    }
  }

  @Nested
  @DisplayName("productContent(내용)가")
  class Describe_productContent {

    @Nested
    @DisplayName("1000자가 초과하면")
    class Context_Over1000Words {

      @Test
      @DisplayName("InvalidContentException를 던진다")
      void it_throwInvalidContentException(){
        String str = "일이삼사오육칠팔구십";
        ProductWriteDto productWriteDto = new ProductWriteDto(
            "테스트",
            str.repeat(101),
            1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidContentException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("값이 없으면")
    class Context_IsBlank {

      @Test
      @DisplayName("InvalidContentException를 던진다.")
      void it_throwInvalidContentException(){
        ProductWriteDto productWriteDto = new ProductWriteDto(
            "테스트",
            "", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidContentException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("openingBid(시작가)가")
  class Describe_openingBid {

    @Nested
    @DisplayName("1보다 작으면")
    class Context_Under1 {

      @Test
      @DisplayName("InvalidOpeningBidException를 던진다.")
      void it_throwInvalidOpeningBidException() {
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 0, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidOpeningBidException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("희망가보다 크다면")
    class Context_BiggerThanHopePrice {

      @Test
      @DisplayName("InvalidOpeningBidException를 던진다.")
      void it_throwInvalidOpeningBidException(){
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 100000, 10,
            1000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidOpeningBidException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("tick(입찰가 단위)이")
  class Describe_tick {

    @Nested
    @DisplayName("1보다 작으면")
    class Context_Under1 {

      @Test
      @DisplayName("InvalidTickException를 던진다.")
      void it_throwInvalidTickException(){
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 0,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidTickException.class,
            () -> sut.saveProduct(productWriteDto, mList));
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
      void it_throwInvalidRepresentPictureIndexException(){
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, -1, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidRepresentPictureIndexException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("이미지 개수보다 크면")
    class Context_OverImageCount {

      @Test
      @DisplayName("InvalidRepresentPictureIndexException를 던진다.")
      void it_throwInvalidRepresentPictureIndexException(){
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 1, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidRepresentPictureIndexException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("category(카테고리 ID)가")
  class Describe_category {

    @Nested
    @DisplayName("존재하지 않을 때")
    class Context_NotExistId {

      @Test
      @DisplayName("InvalidCategoryException를 던진다")
      void it_throwInvalidCategoryException(){
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 0, 99,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidCategoryException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("최하위 카테고리가 아닐 때")
    class Context_NotLastSubCategory {

      @Test
      @DisplayName("InvalidCategoryException를 던진다")
      void it_throwInvalidCategoryException(){
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 0, 1,
            LocalDateTime.now().plusHours(1));
        assertThrows(InvalidCategoryException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

  }

  @Nested
  @DisplayName("expirationDate(기간 만료일)가")
  class Describe_expirationDate {

    @Nested
    @DisplayName("오늘보다 이전일 때")
    class Context_BeforeToday {

      @Test
      @DisplayName("InvalidExpirationDateException를 던진다.")
      void it_throwInvalidExpirationDateException() {
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().minusHours(1));
        assertThrows(InvalidExpirationDateException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("72시간보다 뒤일 때")
    class Context_After72Hours {

      @Test
      @DisplayName("InvalidExpirationDateException를 던진다")
      void it_throwInvalidExpirationDateException(){
        // 서버에서는 시차가 존재해서 -9를 해주었지만
        // 로컬에서는 현재시간이 그대로 적용이기 때문에 72 + 9 + 1의 시간을 더해줌
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(82));
        assertThrows(InvalidExpirationDateException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("files(이미지)가")
  class Describe_files {

    @Nested
    @DisplayName("없을 때")
    class Context_NotExist {

      @Test
      @DisplayName("NoImageException을 던진다.")
      void it_throwNoImageException() {
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertThrows(NoImageException.class,
            () -> sut.saveProduct(productWriteDto, null));
      }
    }

    @Nested
    @DisplayName("확장자가 다를 때")
    class Context_DifferentExtension {

      @Test
      @DisplayName("InvalidExtensionException을 던진다.")
      void it_throwInvalidExtensionException() throws IOException {
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 1000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        List<MultipartFile> mList = makeFailImageList();
        assertThrows(InvalidExtensionException.class,
            () -> sut.saveProduct(productWriteDto, mList));
      }
    }
  }

  @Nested
  @DisplayName("글쓰기 정보를 제대로 입력했을 시")
  class Describe_productSave {

    @Nested
    @DisplayName("희망가가 없을 때")
    class Context_NotExistHopePrice {

      @Test
      @DisplayName("성공한다.")
      void it_success() {
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 100000, 10,
            null, 0, 4,
            LocalDateTime.now().plusHours(1));

        assertDoesNotThrow(() -> sut.saveProduct(productWriteDto, mList));
      }
    }

    @Nested
    @DisplayName("희망가가 있을 때")
    class Context_ExistHopePrice {

      @Test
      @DisplayName("성공한다.")
      void it_success() {
        ProductWriteDto productWriteDto = new ProductWriteDto("테스트", "테스트", 100000, 10,
            100000L, 0, 4,
            LocalDateTime.now().plusHours(1));
        assertDoesNotThrow(() -> sut.saveProduct(productWriteDto, mList));
      }
    }
  }

}
