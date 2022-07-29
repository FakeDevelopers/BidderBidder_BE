package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.PageListResponseDto;
import com.fakedevelopers.bidderbidder.dto.ProductInformationDto;
import com.fakedevelopers.bidderbidder.dto.ProductListDto;
import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.dto.ProductSearchCountDto;
import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.exception.InvalidExpirationDateException;
import com.fakedevelopers.bidderbidder.exception.InvalidExtensionException;
import com.fakedevelopers.bidderbidder.exception.InvalidHopePriceException;
import com.fakedevelopers.bidderbidder.exception.InvalidRepresentPictureIndexException;
import com.fakedevelopers.bidderbidder.exception.InvalidSearchTypeException;
import com.fakedevelopers.bidderbidder.model.FileEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.repository.FileRepository;
import com.fakedevelopers.bidderbidder.repository.ProductRepository;
import com.fakedevelopers.bidderbidder.repository.RedisRepository;
import imageUtil.Image;
import imageUtil.ImageLoader;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;

@Service
public class ProductService {

    private static final String UPLOAD_FOLDER = "./upload";
    private static final String IMAGE_TYPE = "image/jpg";
    private final ResourceLoader resourceLoader;
    private final ProductRepository productRepository;
    private final FileRepository fileRepository;
    private final RedisRepository redisRepository;
    private final ArrayList<String> extensionList = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png"));

    ProductService(ProductRepository productRepository, ResourceLoader resourceLoader, FileRepository fileRepository,
                   RedisRepository redisRepository) {
        this.productRepository = productRepository;
        this.resourceLoader = resourceLoader;
        this.fileRepository = fileRepository;
        this.redisRepository = redisRepository;
    }

    // 게시글 저장
    public void saveProduct(ProductWriteDto productWriteDto, List<MultipartFile> files) throws Exception {

        compareBids(productWriteDto.getHopePrice(), productWriteDto.getOpeningBid());
        compareDate(productWriteDto.getExpirationDate());
        if (files != null) {
            compareExtension(files);
            imageCount(productWriteDto.getRepresentPicture(), files);
        }
        List<String> pathList = createPathIfNeed();
        ProductEntity productEntity = new ProductEntity(pathList.get(0), productWriteDto, files);
        ProductEntity savedProductEntity = productRepository.save(productEntity);
        if (files != null) {
            FileEntity representFileEntity = savedProductEntity.getFileEntities().get(productWriteDto.getRepresentPicture());
            saveResizeFile(representFileEntity.getSavedFileName(), savedProductEntity.getProductId(), pathList);
        }
    }

    // 입력 받은 이미지를 web, app 에 맞게 각각 리사이징 후 저장
    private void saveResizeFile(String fileName, Long productId, List<String> pathList) throws IOException {
        File representImage = new File(UPLOAD_FOLDER, fileName);
        String newFileName = Long.toString(productId);
        resizeImage(newFileName, pathList.get(1), Constants.APP_RESIZE_SIZE, representImage);
        resizeImage(newFileName, pathList.get(2), Constants.WEB_RESIZE_SIZE, representImage);
    }

    // 시작가가 희망가보다 적은지 확인
    private void compareBids(Long hopePrice, long openingBid) {
        if (hopePrice != null && hopePrice < openingBid) {
            throw new InvalidHopePriceException("희망가가 시작가보다 적어 ㅠㅠ ");
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
                    throw new InvalidExtensionException("파일 확장자가 다릅니다.");
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

    // 파일 경로 생성
    private List<String> createPathIfNeed() throws IOException {
        String realPath = UPLOAD_FOLDER;
        String resizeAppPath = realPath + File.separator + "resize_app";
        String resizeWebPath = realPath + File.separator + "resize_web";
        Files.createDirectories(Path.of(resizeAppPath));
        Files.createDirectories(Path.of(resizeWebPath));
        return Arrays.asList(realPath, resizeAppPath, resizeWebPath);
    }

    // 요청받은 내용을 기반으로 페이지네이션 상품리스트 프론트에 반환
    public PageListResponseDto makePageListResponseDto(ProductListRequestDto productListRequestDto, int page) {
        return createPageProductLists(productListRequestDto, page);
    }

    // 요청받은 내용을 기반으로 페이지네이션 상품리스트 생성
    public PageListResponseDto createPageProductLists(ProductListRequestDto productListRequestDto, int page) {
        Pageable pageable = PageRequest.of(page - 1, productListRequestDto.getListCount(), Sort.Direction.DESC, "productId");
        String searchWord = productListRequestDto.getSearchWord();
        int searchType = productListRequestDto.getSearchType();

        return searchWord == null || searchWord.trim().equals("") ? makeProductList(pageable) : makeProductList(searchWord, searchType, pageable);
    }

    // 요청받은 내용을 기반으로 무한스크롤 상품리스트 생성 및 프론트에 반환
    public List<ProductListDto> createInfiniteProductLists(ProductListRequestDto productListRequestDto, long startNumber) {
        int size = productListRequestDto.getListCount();
        ProductEntity productEntity = productRepository.findTopByOrderByProductIdDesc();
        long maxCount = productEntity.getProductId();
        if (startNumber == -1) {
            startNumber = maxCount + 1;
        }
        String searchWord = productListRequestDto.getSearchWord();
        int searchType = productListRequestDto.getSearchType();

        return searchWord == null || searchWord.trim().equals("") ? makeProductList(size, startNumber) : makeProductList(searchWord, searchType, size, startNumber);
    }

    // 검색어 없을 때 페이지네이션으로 상품 리스트 만들기
    private PageListResponseDto makeProductList(Pageable pageable) {
        List<ProductEntity> productList = productRepository.findAllBy(pageable);

        return new PageListResponseDto(productRepository.count(), addItemList(productList, true));
    }

    // 검색어 없을 때 무한스크롤로 상품 리스트 만들기
    private List<ProductListDto> makeProductList(int size, long startNumber) {
        List<ProductEntity> productList = productRepository.findAllByProductIdIsLessThanOrderByProductIdDesc(startNumber, PageRequest.of(0, size));
        return addItemList(productList, false);
    }

    // 검색어 있을 때 페이지네이션으로 상품 리스트 만들기
    private PageListResponseDto makeProductList(String searchWord, int searchType, Pageable pageable) {
        redisRepository.saveSearchWord(searchWord);
        ProductSearchCountDto productList = searchProduct(searchWord, searchType, pageable);

        return new PageListResponseDto(productList.getItemCount(), addItemList(productList.getItems(), true));
    }

    // 검색어 있을 때 무한스크롤로 상품 리스트 만들기
    private List<ProductListDto> makeProductList(String searchWord, int searchType, int size, long startNumber) {
        redisRepository.saveSearchWord(searchWord);
        Pageable pageable = PageRequest.of(0, size);
        List<ProductEntity> productList = searchProduct(searchWord, searchType, startNumber, pageable);
        return addItemList(productList, false);
    }

    // 상품 리스트 만들어줌
    private ArrayList<ProductListDto> addItemList(List<ProductEntity> productList, Boolean isWeb) {
        ArrayList<ProductListDto> itemList = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        for (ProductEntity productEntity : productList) {
            int randomZeroNine = random.nextInt(10);
            itemList.add(new ProductListDto(productEntity.getProductId(), "/product/getThumbnail?productId=" + productEntity.getProductId() + "&isWeb=" + isWeb,
                    productEntity.getProductTitle(), productEntity.getHopePrice(), productEntity.getOpeningBid(), productEntity.getTick(),
                    productEntity.getExpirationDate().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)), randomZeroNine * 3));
        }
        return itemList;
    }

    // 실제 이미지 리사이징 후, 프론트에 보내줌
    // 이미지가 없다면 X표 이미지가 나옴
    public ResponseEntity<Resource> getThumbnail(Long productId, boolean isWeb) throws IOException {
        InputStream inputStream;
        String imagePath = UPLOAD_FOLDER + File.separator + (isWeb ? "resize_web" : "resize_app");
        File image = new File(imagePath + "/resize_" + productId + ".jpg");
        if (image.exists()) {
            inputStream = new FileInputStream(image);
        } else {
            inputStream = resourceLoader.getResource("classpath:/static/img/noImage.jpg").getInputStream();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, IMAGE_TYPE);
        Resource resource = new InputStreamResource(inputStream);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    public ResponseEntity<Resource> getProductImage(Long fileId) throws IOException {
        FileEntity fileEntity = fileRepository.findByFileId(fileId);
        if (fileEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        InputStream inputStream;
        File image = new File(UPLOAD_FOLDER + File.separator + fileEntity.getSavedFileName());
        inputStream = new FileInputStream(image);


        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, IMAGE_TYPE);
        Resource resource = new InputStreamResource(inputStream);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    // 중고 자전거.jpg로 리사이징 후 프론트에 보내줌
    public ResponseEntity<Resource> checkResizeImage(int width) throws IOException {

        InputStream file = resourceLoader.getResource("classpath:/static/img/중고 자전거.jpg").getInputStream();

        String imageName = "중고 자전거.jpg";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, IMAGE_TYPE);
        Resource resource = new InputStreamResource(new FileInputStream(resizeImage(imageName, width, file)));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }

    // 실제 이미지 리사이징 후 저장
    private void resizeImage(String imageName, String path, int width, File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        Image img = ImageLoader.fromStream(inputStream);

        int shortLen = min(img.getWidth(), img.getHeight());
        if (shortLen < width) {
            width = shortLen;
        }
        Image resizedImg = img.getResizedToSquare(width, 0.0);
        inputStream.close();

        File resizedFile = new File(path, "resize_" + imageName);
        resizedImg.writeToFile(resizedFile);
    }

    // 중고 자전거.jpg로 리사이징 크기 확인
    private File resizeImage(String imageName, int width, InputStream file) throws IOException {
        Image img = ImageLoader.fromStream(file);

        int shortLen = min(img.getWidth(), img.getHeight());
        if (shortLen < width) {
            width = shortLen;
        }
        Image resizedImg = img.getResizedToSquare(width, 0.0);

        File resizedFile = new File("resize_" + imageName);

        return resizedImg.writeToFile(resizedFile);
    }

    // 게시글 상세 내용 얻어오기
    public ResponseEntity<ProductInformationDto> getProductInfo(long productId) {

        ProductEntity productEntity = productRepository.findByProductId(productId);
        if (productEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<FileEntity> fileEntities = productEntity.getFileEntities();
        ArrayList<String> images = new ArrayList<>();
        String url = "/product/getImage/";
        for (FileEntity fileEntity : fileEntities) {
            images.add(url + fileEntity.getFileId());
        }

        SecureRandom random = new SecureRandom();
        int randomZeroNine = random.nextInt(10);
        ProductInformationDto productInformationDto = new ProductInformationDto(
                productEntity, randomZeroNine * 3, images);
        return new ResponseEntity<>(productInformationDto, HttpStatus.OK);
    }

    // 페이지네이션 searchProduct
    public ProductSearchCountDto searchProduct(String searchWord, int searchType, Pageable pageable) {
        switch (searchType) {
            // 제목에서 searchWord 포함하는 상품 검색
            case 0:
                return new ProductSearchCountDto(productRepository.countAllByProductTitleContainingIgnoreCase(searchWord),
                        productRepository.findAllByProductTitleContainingIgnoreCase(searchWord, pageable));
            // 내용에서 searchWord 포함하는 상품 검색
            case 1:
                return new ProductSearchCountDto(productRepository.countAllByProductContentContainingIgnoreCase(searchWord),
                        productRepository.findAllByProductContentContainingIgnoreCase(searchWord, pageable));
            // 제목+내용에서 searchWord 포함하는 상품 검색
            case 2:
                return new ProductSearchCountDto(productRepository.countAllByProductTitleContainingIgnoreCaseOrProductContentContainingIgnoreCase(searchWord, searchWord),
                        productRepository.findAllByProductTitleContainingIgnoreCaseOrProductContentContainingIgnoreCase(searchWord, searchWord, pageable));
            default:
                throw new InvalidSearchTypeException("검색 타입이 잘못되었습니다.");
        }
    }


    // 무한 스크롤 searchProduct
    public List<ProductEntity> searchProduct(String searchWord, int searchType, long startNumber, Pageable pageable) {

        switch (searchType) {
            // 제목에서 searchWord 포함하는 상품 검색
            case 0:
                return productRepository.findAllByProductTitleContainingIgnoreCaseAndProductIdIsLessThanOrderByProductIdDesc(searchWord, startNumber, pageable);
            // 내용에서 searchWord 포함하는 상품 검색
            case 1:
                return productRepository.findAllByProductContentContainingIgnoreCaseAndProductIdIsLessThanOrderByProductIdDesc(searchWord, startNumber, pageable);
            // 제목+내용에서 searchWord 포함하는 상품 검색
            case 2:
                return productRepository.searchProductByTitleAndContentInInfiniteScroll(searchWord, startNumber, pageable);
            default:
                throw new InvalidSearchTypeException("검색 타입이 잘못되었습니다.");

        }
    }

    // 인기 검색어 가져오기
    public List<String> getPopularSearchWord(int listCount) {
        return redisRepository.getPopularSearchWord(listCount);
    }

    // 매일 00시마다 메모리에 리스트 저장 및 레디스 초기화
    @Scheduled(cron = "0 0 0 * * *")
    private void deleteSearchWords() {
        redisRepository.deleteSearchWords();
    }

    // 게시글 검색
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

}
