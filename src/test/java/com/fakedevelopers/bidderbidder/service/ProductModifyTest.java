package com.fakedevelopers.bidderbidder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.dto.ProductInfoDto;
import com.fakedevelopers.bidderbidder.exception.ModifyProductException;
import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.CategoryRepository;
import com.fakedevelopers.bidderbidder.repository.FileRepository;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("글쓰기 수정 테스트")
public class ProductModifyTest extends IntegrationTestBase {

  private static ResourceLoader testResourceLoader;
  private static List<MultipartFile> modifyList;
  private static UserEntity myUserEntity;
  private static UserEntity savedUserEntity;
  private static long productID, productID2;
  @Autowired
  ProductService productService;
  @Autowired
  BidService bidService;
  @Autowired
  UserService userService;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  CategoryRepository categoryRepository;

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
      @Autowired ProductRepository productRepository,
      @Autowired CategoryRepository categoryRepository,
      @Autowired UserService userService) throws Exception {
    testResourceLoader = resourceLoader;
    modifyList = makeImageList("5wlq.", "jpg");
    myUserEntity = userService.register(OAuth2UserRegisterDto.builder().username("myUser").
        email("a@b.com").nickname("my" + Constants.INIT_NICKNAME).build());

    List<MultipartFile> savedList = makeImageList("Starry night.", "jpg");
    List<MultipartFile> savedList2 = makeImageList("나선의 달.", "jpg");

    savedUserEntity = userService.register(OAuth2UserRegisterDto.builder().username("savedUser").
        email("c@d.com").nickname("saved" + Constants.INIT_NICKNAME).build());
    ProductInfoDto productInfoDto = new ProductInfoDto("테스트", "테스트",
        1000, 10, 100000L, 0, 4,
        LocalDateTime.now().plusHours(1));
    CategoryEntity categoryEntity = categoryRepository.getById(4L);

    ProductEntity product = new ProductEntity(Constants.UPLOAD_FOLDER, productInfoDto, savedList,
        categoryEntity, savedUserEntity);
    ProductEntity product2 = new ProductEntity(Constants.UPLOAD_FOLDER, productInfoDto, savedList2,
        categoryEntity, savedUserEntity);
    productID = productRepository.save(product).getProductId();
    productID2 = productRepository.save(product2).getProductId();
  }

  @AfterAll
  static void tearDown(@Autowired ProductRepository productRepository) {
    productRepository.deleteById(productID);
    productRepository.deleteById(productID2);
  }

  private void deleteImage(long productId) {
    String originalImage = productRepository.findByProductId(productId).getFileEntities().get(0)
        .getSavedFileName();
    String resizedImage =
        Constants.RESIZE + productRepository.findByProductId(productId).getProductId() + ".jpg";
    File originalFile = new File(Constants.UPLOAD_FOLDER + File.separator + originalImage);
    File appResizedFile = new File(
        Constants.UPLOAD_FOLDER + File.separator + Constants.RESIZE_APP + File.separator + resizedImage);
    File webResizedFile = new File(
        Constants.UPLOAD_FOLDER + File.separator + Constants.RESIZE_WEB + File.separator + resizedImage);

    originalFile.delete();
    appResizedFile.delete();
    webResizedFile.delete();
  }

  @Nested
  @DisplayName("게시글에서")
  class Describe_ProductEntity {

    @Nested
    @DisplayName("입찰자가 이미 있다면")
    class Context_HaveBidder {

      @Test
      @DisplayName("ModifyProductException을 던진다.")
      void it_throwInvalidOpeningBidException() {

        long userID = 1;
        long bid = 10000;
        bidService.addBid(productID, userID, bid);

        ProductInfoDto productInfoDto = new ProductInfoDto("수정 테스트", "수정 테스트", 10000, 100,
            null, 0, 5,
            LocalDateTime.now().plusHours(5));

        assertThrows(ModifyProductException.class,
            () -> productService.modifyProduct(savedUserEntity, productInfoDto, modifyList,
                productID));
      }
    }

    @Nested
    @DisplayName("작성자가 로그인한 유저와 다르다면")
    class Context_NotEqualWriteAndLoginUser {

      @Test
      @DisplayName("ModifyProductException을 던진다.")
      void it_throwInvalidOpeningBidException() {

        ProductInfoDto productInfoDto = new ProductInfoDto("수정 테스트", "수정 테스트", 10000, 100,
            null, 0, 5,
            LocalDateTime.now().plusHours(5));

        assertThrows(ModifyProductException.class,
            () -> productService.modifyProduct(myUserEntity, productInfoDto, modifyList,
                productID));
      }
    }
  }

  @Nested
  @DisplayName("글쓰기 정보를 제대로 입력했을 시")
  class Describe_productSave {

    @Nested
    @DisplayName("희망가가 있으면")
    class Context_ExistHopePrice {

      @Test
      @DisplayName("성공한다.")
      void it_success() throws Exception {
        ProductInfoDto productInfoDto = new ProductInfoDto("수정 테스트2", "수정 테스트2", 20000, 200,
            2000000L, 0, 5,
            LocalDateTime.now().plusHours(2));
        ProductEntity product = productService.modifyProduct(savedUserEntity, productInfoDto,
            modifyList, productID2);

        assertThat(product.getProductId()).isEqualTo(productID2);
        assertThat(product.getProductTitle()).isEqualTo(productInfoDto.getProductTitle());
        assertThat(product.getProductContent()).isEqualTo(productInfoDto.getProductContent());
        assertThat(product.getOpeningBid()).isEqualTo(productInfoDto.getOpeningBid());
        assertThat(product.getTick()).isEqualTo(productInfoDto.getTick());
        assertThat(product.getHopePrice()).isEqualTo(productInfoDto.getHopePrice());
        assertThat(product.getRepresentPicture()).isEqualTo(productInfoDto.getRepresentPicture());
        assertThat(product.getCategory().getCategoryId()).isEqualTo(productInfoDto.getCategory());

        deleteImage(productID2);
      }
    }

    @Nested
    @DisplayName("희망가가 없으면")
    class Context_NotExistHopePrice {

      @Test
      @DisplayName("성공한다.")
      void it_success() throws Exception {
        ProductInfoDto productInfoDto = new ProductInfoDto("수정 테스트", "수정 테스트", 10000, 100,
            null, 0, 5,
            LocalDateTime.now().plusHours(3));
        ProductEntity product = productService.modifyProduct(savedUserEntity, productInfoDto,
            modifyList, productID);

        assertThat(product.getProductId()).isEqualTo(productID);
        assertThat(product.getProductTitle()).isEqualTo(productInfoDto.getProductTitle());
        assertThat(product.getProductContent()).isEqualTo(productInfoDto.getProductContent());
        assertThat(product.getOpeningBid()).isEqualTo(productInfoDto.getOpeningBid());
        assertThat(product.getTick()).isEqualTo(productInfoDto.getTick());
        assertThat(product.getHopePrice()).isEqualTo(productInfoDto.getHopePrice());
        assertThat(product.getRepresentPicture()).isEqualTo(productInfoDto.getRepresentPicture());
        assertThat(product.getCategory().getCategoryId()).isEqualTo(productInfoDto.getCategory());

        deleteImage(productID);
      }
    }
  }

}
