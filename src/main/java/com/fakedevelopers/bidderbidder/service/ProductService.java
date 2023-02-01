package com.fakedevelopers.bidderbidder.service;

import static java.lang.Math.min;
import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.PageListResponseDto;
import com.fakedevelopers.bidderbidder.dto.ProductInformationDto;
import com.fakedevelopers.bidderbidder.dto.ProductListDto;
import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.exception.FileDeleteException;
import com.fakedevelopers.bidderbidder.exception.InvalidCategoryException;
import com.fakedevelopers.bidderbidder.exception.InvalidExpirationDateException;
import com.fakedevelopers.bidderbidder.exception.InvalidExtensionException;
import com.fakedevelopers.bidderbidder.exception.InvalidOpeningBidException;
import com.fakedevelopers.bidderbidder.exception.InvalidRepresentPictureIndexException;
import com.fakedevelopers.bidderbidder.exception.ModifyProductException;
import com.fakedevelopers.bidderbidder.exception.NoImageException;
import com.fakedevelopers.bidderbidder.exception.NotFoundImageException;
import com.fakedevelopers.bidderbidder.exception.NotFoundProductException;
import com.fakedevelopers.bidderbidder.exception.ProductNotFoundException;
import com.fakedevelopers.bidderbidder.model.BidEntity;
import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import com.fakedevelopers.bidderbidder.model.FileEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.BidRepository;
import com.fakedevelopers.bidderbidder.repository.CategoryRepository;
import com.fakedevelopers.bidderbidder.repository.FileRepository;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.RedisRepository;
import imageUtil.Image;
import imageUtil.ImageLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

  private static final String UPLOAD_FOLDER = "./upload";
  private static final String IMAGE_TYPE = "image/jpg";
  private final ResourceLoader resourceLoader;
  private final ProductRepository productRepository;
  private final FileRepository fileRepository;
  private final RedisRepository redisRepository;
  private final CategoryRepository categoryRepository;
  private final BidRepository bidRepository;
  private final ArrayList<String> extensionList =
      new ArrayList<>(Arrays.asList("jpg", "jpeg", "png", "gif"));

  ProductService(
      ProductRepository productRepository,
      ResourceLoader resourceLoader,
      FileRepository fileRepository,
      RedisRepository redisRepository,
      CategoryRepository categoryRepository,
      BidRepository bidRepository) {
    this.productRepository = productRepository;
    this.resourceLoader = resourceLoader;
    this.fileRepository = fileRepository;
    this.redisRepository = redisRepository;
    this.categoryRepository = categoryRepository;
    this.bidRepository = bidRepository;
  }

  /**
   * . 게시글 저장
   */
  public ProductEntity saveProduct(UserEntity userEntity, ProductWriteDto productWriteDto,
      List<MultipartFile> files)
      throws Exception {

    if (files == null) {
      throw new NoImageException();
    }
    saveProductValidation(productWriteDto, files);
    List<String> pathList = createPathIfNeed();
    CategoryEntity categoryEntity = categoryRepository.getById(productWriteDto.getCategory());

    ProductEntity productEntity =
        new ProductEntity(pathList.get(0), productWriteDto, files, categoryEntity, userEntity);
    ProductEntity savedProductEntity = productRepository.save(productEntity);

    FileEntity representFileEntity =
        savedProductEntity.getFileEntities().get(productWriteDto.getRepresentPicture());
    saveResizeFile(
        representFileEntity.getSavedFileName(), savedProductEntity.getProductId(),
        pathList);
    return savedProductEntity;
  }

  public ProductEntity modifyProduct(UserEntity userEntity, ProductWriteDto productWriteDto,
      List<MultipartFile> files, long productId) throws Exception {
    modifyProductValidation(userEntity, productWriteDto, files, productId);
    ProductEntity productEntity = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    fileRepository.deleteAllInBatch(productEntity.getFileEntities());
    deleteImage(productId);

    List<String> pathList = createPathIfNeed();
    CategoryEntity categoryEntity = categoryRepository.getById(productWriteDto.getCategory());
    productEntity.modify(pathList.get(0), productWriteDto, files, categoryEntity);
    productRepository.save(productEntity);

    FileEntity representFileEntity =
        productEntity.getFileEntities().get(productWriteDto.getRepresentPicture());
    saveResizeFile(
        representFileEntity.getSavedFileName(), productEntity.getProductId(),
        pathList);

    return productEntity;
  }

  private void deleteImage(long productId) {
    String path = UPLOAD_FOLDER;
    ProductEntity productEntity = productRepository.findByProductId(productId);
    for (FileEntity file : productEntity.getFileEntities()) {
      String originalImage = file.getSavedFileName();
      File originalFile = new File(path + File.separator + originalImage);
      if(!originalFile.delete()){
        throw new FileDeleteException("파일 삭제 실패");
      }
    }

    String resizedImage =
        Constants.RESIZE + productRepository.findByProductId(productId).getProductId() + ".jpg";
    File appResizedFile = new File(
        path + File.separator + Constants.RESIZE_APP + File.separator + resizedImage);
    File webResizedFile = new File(
        path + File.separator + Constants.RESIZE_WEB + File.separator + resizedImage);

    if(!appResizedFile.delete() || !webResizedFile.delete()){
      throw new FileDeleteException("파일 삭제 실패");
    }
  }

  private void saveProductValidation(ProductWriteDto productWriteDto, List<MultipartFile> files) {
    checkOpeningBid(productWriteDto.getHopePrice(), productWriteDto.getOpeningBid());
    compareDate(productWriteDto.getExpirationDate());
    checkCategoryId(productWriteDto.getCategory());
    checkLastSubCategory(productWriteDto.getCategory());
    compareExtension(files);
    imageCount(productWriteDto.getRepresentPicture(), files);
  }

  private void modifyProductValidation(UserEntity userEntity, ProductWriteDto productWriteDto,
      List<MultipartFile> files, long productId) {
    if (!bidRepository.getBidsByProductId(productId).isEmpty()) {
      throw new ModifyProductException("입찰자가 있어서 수정할 수 없습니다.");
    }
    // UserEntity userId 와 해당 게시글 user_id가 같은지 확인하는 코드드
    if (!Objects.equals(productRepository.getById(productId).getUser().getId(),
        userEntity.getId())) {
      throw new ModifyProductException("게시글 작성자와 로그인한 유저가 다릅니다.");
    }
    saveProductValidation(productWriteDto, files);
  }

  // 입력 받은 이미지를 web, app 에 맞게 각각 리사이징 후 저장
  private void saveResizeFile(String fileName, Long productId,
      List<String> pathList)
      throws IOException {
    File representImage = new File(UPLOAD_FOLDER, fileName);
    String newFileName = Long.toString(productId);
    resizeImage(newFileName, pathList.get(1), Constants.APP_RESIZE_SIZE,
        representImage);
    resizeImage(newFileName, pathList.get(2), Constants.WEB_RESIZE_SIZE,
        representImage);
  }

  // 시작가가 희망가보다 적은지 확인
  private void checkOpeningBid(Long hopePrice, long openingBid) {
    if (openingBid < 1) {
      throw new InvalidOpeningBidException("시작가는 1이상이어야 합니다.");
    }
    if (hopePrice != null && hopePrice < openingBid) {
      throw new InvalidOpeningBidException("시작가가 희망가보다 큽니다.");
    }
  }

  // 경매 마감 날짜가 지금 날짜보다 나중인지 확인
  private void compareDate(LocalDateTime expirationDate) {
    long betweenHour = ChronoUnit.HOURS.between(LocalDateTime.now(), expirationDate) - 9;
    long betweenMinute = ChronoUnit.MINUTES.between(LocalDateTime.now(), expirationDate);
    if (expirationDate.isBefore(LocalDateTime.now())) {
      throw new InvalidExpirationDateException("마감 날짜가 지금 날짜보다 빨라요");
    } else if (betweenHour > 72 || (betweenHour == 72 && betweenMinute != 0)) {
      throw new InvalidExpirationDateException("마감 날짜는 최대 72시간 후입니다.");
    }
  }

  // 파일 확장자 jpg, png, jpeg 아니면 안되게 했음 -> 확장자도 얘기 후 조정
  // 특정 확장자만 선택하게 하는건 프론트엔드에서도 가능
  // 백엔드에서도 확인하는게 좋으니 양쪽에서 특정 확장자를 맞춰야겠네요
  private void compareExtension(List<MultipartFile> files) {
    if (files != null) {
      for (MultipartFile file : files) {
        String fileOriginName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileOriginName);

        if (!extensionList.contains(extension)) {
          throw new InvalidExtensionException();
        }
      }
    }
  }

  // 대표이미지 index가 유효하지 않으면 예외처리
  private void imageCount(int representPicture, List<MultipartFile> files) {
    if (representPicture < 0) {
      throw new InvalidRepresentPictureIndexException("대표 사진은 0번째보다 낮을 수 없습니다.");
    }
    if (representPicture > files.size() - 1) {
      throw new InvalidRepresentPictureIndexException("사진의 개수보다 큰 값을 입력했습니다.");
    }
  }

  // 카테고리 id가 유효한지 체크
  private void checkCategoryId(long categoryId) {
    if (categoryRepository.findById(categoryId).isEmpty()) {
      throw new InvalidCategoryException("잘못된 카테고리 번호입니다.");
    }
  }

  private void checkLastSubCategory(long categoryId) {
    CategoryEntity category = categoryRepository.getById(categoryId);
    if (!category.getSubCategories().isEmpty()) {
      throw new InvalidCategoryException("최하위 카테고리가 아닙니다.");
    }
  }

  // 파일 경로 생성
  private List<String> createPathIfNeed() throws IOException {
    String realPath = UPLOAD_FOLDER;
    String resizeAppPath = realPath + File.separator + Constants.RESIZE_APP;
    String resizeWebPath = realPath + File.separator + Constants.RESIZE_WEB;
    Files.createDirectories(Path.of(resizeAppPath));
    Files.createDirectories(Path.of(resizeWebPath));
    return Arrays.asList(realPath, resizeAppPath, resizeWebPath);
  }

  public PageListResponseDto getProductListPagination(
      ProductListRequestDto productListRequestDto, int page) {
    checkCategoryIdIncludeZero(productListRequestDto.getCategory());
    Pageable pageable =
        PageRequest.of(
            page - 1, productListRequestDto.getListCount(), Sort.Direction.DESC,
            "productId");
    List<ProductEntity> productList = getProductList(null, productListRequestDto, pageable);

    List<ProductListDto> productListDto = makeProductListDtoList(productList, true);
    return new PageListResponseDto(productListDto.size(), productListDto);
  }

  public List<ProductListDto> getProductListInfiniteScroll(
      ProductListRequestDto productListRequestDto, long startNumber) {
    checkCategoryIdIncludeZero(productListRequestDto.getCategory());
    int size = productListRequestDto.getListCount();
    ProductEntity productEntity = productRepository.findTopByOrderByProductIdDesc();
    long maxCount = productEntity.getProductId();
    if (startNumber == -1) {
      startNumber = maxCount + 1;
    }
    Pageable pageable = PageRequest.of(0, size);
    List<ProductEntity> productList = getProductList(startNumber, productListRequestDto,
        pageable);

    return makeProductListDtoList(productList, false);
  }

  private void checkCategoryIdIncludeZero(long category) {
    if (category != 0) {
      checkCategoryId(category);
    }
  }

  public List<ProductEntity> getProductList(
      Long startNumber, ProductListRequestDto productListRequestDto, Pageable pageable) {

    long categoryId = productListRequestDto.getCategory();
    if (categoryId == 0 || categoryRepository.getById(categoryId).getSubCategories()
        .isEmpty()) {
      return productRepository.findAllProduct(startNumber, productListRequestDto, pageable);
    } else {
      return getSubCategories(startNumber, productListRequestDto, pageable);
    }
  }

  private List<ProductEntity> getSubCategories(
      Long startNumber, ProductListRequestDto productListRequestDto, Pageable pageable) {
    List<Long> subCategoryId =
        categoryRepository.findAllSubCategoryId(productListRequestDto.getCategory());
    List<ProductEntity> productList = new ArrayList<>();

    for (Long subCategory : subCategoryId) {
      if (categoryRepository.getById(subCategory).getSubCategories().isEmpty()) {
        productListRequestDto.setCategory(subCategory);
        productList.addAll(
            productRepository.findAllProduct(startNumber, productListRequestDto,
                pageable));
      }
    }
    return productList;
  }

  private List<ProductListDto> makeProductListDtoList(
      List<ProductEntity> productList, boolean isWeb) {
    return productList.stream()
        .map(
            (it) ->
                new ProductListDto(
                    it, isWeb,
                    bidRepository.getBidsByProductId(it.getProductId()).size()))
        .collect(Collectors.toList());
  }

  /**
   * . 실제 이미지 리사이징 후, 프론트에 보내줌 이미지가 없다면 X표 이미지가 나옴
   */
  public ResponseEntity<Resource> getThumbnail(Long productId, boolean isWeb) throws IOException {
    InputStream inputStream;
    String imagePath = UPLOAD_FOLDER + File.separator + (isWeb ? Constants.RESIZE_WEB : Constants.RESIZE_APP);
    File image = new File(imagePath + File.separator + Constants.RESIZE + productId + ".jpg");
    if (image.exists()) {
      inputStream = new FileInputStream(image);
    } else {
      inputStream =
          resourceLoader.getResource("classpath:/static/img/noImage.jpg")
              .getInputStream();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, IMAGE_TYPE);
    Resource resource = new InputStreamResource(inputStream);

    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
  }

  /**
   * . 이미지 ID를 통해 이미지 확인
   */
  public ResponseEntity<Resource> getProductImage(Long fileId) throws IOException {
    FileEntity fileEntity = fileRepository.findByFileId(fileId);
    if (fileEntity == null) {
      throw new NotFoundImageException();
    }
    InputStream inputStream;
    File image = new File(UPLOAD_FOLDER + File.separator + fileEntity.getSavedFileName());
    inputStream = new FileInputStream(image);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, IMAGE_TYPE);
    Resource resource = new InputStreamResource(inputStream);

    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
  }

  /**
   * . 중고 자전거.jpg로 리사이징 후 프론트에 보내줌
   */
  public ResponseEntity<Resource> checkResizeImage(int width) throws IOException {

    InputStream file =
        resourceLoader.getResource("classpath:/static/img/중고 자전거.jpg").getInputStream();

    String imageName = "중고 자전거.jpg";

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, IMAGE_TYPE);
    Resource resource =
        new InputStreamResource(new FileInputStream(resizeImage(imageName, width, file)));

    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
  }

  /**
   * . 실제 이미지 리사이징 후 저장
   */
  private void resizeImage(String imageName, String path, int width, File file)
      throws IOException {
    InputStream inputStream = new FileInputStream(file);
    Image img = ImageLoader.fromStream(inputStream);

    int shortLen = min(img.getWidth(), img.getHeight());
    if (shortLen < width) {
      width = shortLen;
    }
    Image resizedImg = img.getResizedToSquare(width, 0.0);
    inputStream.close();

    File resizedFile = new File(path, Constants.RESIZE + imageName);
    resizedImg.writeToFile(resizedFile);
  }

  /**
   * 중고 자전거.jpg로 리사이징 크기 확인
   */
  private File resizeImage(String imageName, int width, InputStream file) throws IOException {
    Image img = ImageLoader.fromStream(file);

    int shortLen = min(img.getWidth(), img.getHeight());
    if (shortLen < width) {
      width = shortLen;
    }
    Image resizedImg = img.getResizedToSquare(width, 0.0);

    File resizedFile = new File(Constants.RESIZE + imageName);

    return resizedImg.writeToFile(resizedFile);
  }

  /**
   * . 게시글 상세 내용 얻어오기
   */
  public ResponseEntity<ProductInformationDto> getProductInfo(long productId) {

    ProductEntity productEntity = productRepository.findByProductId(productId);
    if (productEntity == null) {
      throw new NotFoundProductException();
    }
    List<FileEntity> fileEntities = productEntity.getFileEntities();
    ArrayList<String> images = new ArrayList<>();
    String url = "/product/getImage/";
    for (FileEntity fileEntity : fileEntities) {
      images.add(url + fileEntity.getFileId());
    }
    List<BidEntity> bids = bidRepository.getBidsByProductId(productId);
    ProductInformationDto productInformationDto =
        new ProductInformationDto(productEntity, images, bids);
    return new ResponseEntity<>(productInformationDto, HttpStatus.OK);
  }

  /**
   * . 인기 검색어 가져오기
   */
  public List<String> getPopularSearchWord(int listCount) {
    return redisRepository.getPopularSearchWord(listCount);
  }

  public List<CategoryEntity> getCategoryLevel1() {
    return categoryRepository.findAllByParentCategoryIdIsNull();
  }

  public void addCategory(String cateName, Long parentCateId) {
    CategoryEntity categoryEntity = new CategoryEntity(cateName, parentCateId);
    categoryRepository.save(categoryEntity);
  }

  /**
   * . 게시글 검색
   */
  public List<ProductEntity> getAllProducts() {
    return productRepository.findAll();
  }
}
