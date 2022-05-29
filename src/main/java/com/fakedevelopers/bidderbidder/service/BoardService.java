package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.BoardWriteDto;
import com.fakedevelopers.bidderbidder.dto.PageListResponseDto;
import com.fakedevelopers.bidderbidder.dto.ProductListDto;
import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.exception.InvalidExpirationDateException;
import com.fakedevelopers.bidderbidder.exception.InvalidExtensionException;
import com.fakedevelopers.bidderbidder.exception.InvalidHopePriceException;
import com.fakedevelopers.bidderbidder.exception.InvalidRepresentPictureIndexException;
import com.fakedevelopers.bidderbidder.model.BoardEntity;
import com.fakedevelopers.bidderbidder.model.FileEntity;
import com.fakedevelopers.bidderbidder.repository.BoardRepository;
import imageUtil.Image;
import imageUtil.ImageLoader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;

@Service
public class BoardService {

    private static final String UPLOAD_FOLDER = "./upload";
    private final ResourceLoader resourceLoader;
    private final BoardRepository boardRepository;
    private final ArrayList<String> extensionList = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png"));

    BoardService(BoardRepository boardRepository, ResourceLoader resourceLoader) {
        this.boardRepository = boardRepository;
        this.resourceLoader = resourceLoader;
    }

    // 게시글 저장
    public void saveBoard(BoardWriteDto boardWriteDto, List<MultipartFile> files) throws Exception {

        compareBids(boardWriteDto.getHopePrice(), boardWriteDto.getOpeningBid());
        compareDate(boardWriteDto.getExpirationDate());
        if (files != null) {
            compareExtension(files);
            imageCount(boardWriteDto.getRepresentPicture(), files);
        }
        List<String> pathList = createPathIfNeed();
        BoardEntity boardEntity = new BoardEntity(pathList.get(0), boardWriteDto, files);
        BoardEntity savedBoardEntity = boardRepository.save(boardEntity);
        if (files != null) {
            FileEntity representFileEntity = savedBoardEntity.getFileEntities().get(boardWriteDto.getRepresentPicture());
            saveResizeFile(representFileEntity.getSavedFileName(), savedBoardEntity.getBoardId(), pathList);
        }
    }

    private void saveResizeFile(String fileName, Long boardId, List<String> pathList) throws Exception {
        File representImage = new File(UPLOAD_FOLDER, fileName);
        String newFileName = Long.toString(boardId);
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
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new InvalidExpirationDateException("마감 날짜가 지금 날짜보다 빨라요");
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

    private void imageCount(int representPicture, List<MultipartFile> files) {
        int size = files.size();
        if (representPicture < 0) {
            throw new InvalidRepresentPictureIndexException("대표 사진은 0번째보다 낮을 수 없습니다.");
        }
        if (representPicture > size - 1) {
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

    public List<ProductListDto> createPageProductLists(ProductListRequestDto productListRequestDto, int page) {
        List<ProductListDto> productList;
        Pageable pageable = PageRequest.of(page, productListRequestDto.getListCount(), Sort.Direction.DESC, "boardId");

        productList = makeProductList(pageable);

        return productList;
    }

    public List<ProductListDto> createInfiniteProductLists(ProductListRequestDto productListRequestDto, long startNumber) {
        List<ProductListDto> productList;

        int size = productListRequestDto.getListCount();
        BoardEntity boardEntity = boardRepository.findTopByOrderByBoardIdDesc();
        long maxCount = boardEntity.getBoardId();
        if (startNumber == -1) {
            startNumber = maxCount;
        }

        productList = makeProductList(size, startNumber);

        return productList;
    }

    public PageListResponseDto makePageListResponseDto(ProductListRequestDto productListRequestDto, int page) {
        return new PageListResponseDto(boardRepository.count(),
                createPageProductLists(productListRequestDto, page));
    }

    private List<ProductListDto> makeProductList(Pageable pageable) {

        List<BoardEntity> boardList = boardRepository.findAllBy(pageable);

        return addItemList(boardList, true);
    }

    private List<ProductListDto> makeProductList(int size, long startNumber) {

        List<BoardEntity> boardList = boardRepository.findAllByBoardIdIsLessThanOrderByBoardIdDesc(startNumber, PageRequest.of(0, size));
        return addItemList(boardList, false);
    }

    private ArrayList<ProductListDto> addItemList(List<BoardEntity> boardList, Boolean isWeb) {
        ArrayList<ProductListDto> itemList = new ArrayList<>();

        SecureRandom random = new SecureRandom();
        for (BoardEntity boardentity : boardList) {
            int randomZeroNine = random.nextInt(10);
            itemList.add(new ProductListDto(boardentity.getBoardId(), "/board/getThumbnail?boardId=" + boardentity.getBoardId() + "&isWeb=" + isWeb,
                    boardentity.getBoardTitle(), boardentity.getHopePrice(), boardentity.getOpeningBid(), boardentity.getTick(),
                    boardentity.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), randomZeroNine * 3));
        }
        return itemList;
    }

    public ResponseEntity<Resource> getThumbnail(Long boardId, Boolean isWeb) throws IOException {

        InputStream inputStream;
        String imagePath = UPLOAD_FOLDER + File.separator + (isWeb ? "resize_web" : "resize_app");
        File image = new File(imagePath + "/resize_" + boardId + ".jpg");
        if (image.exists()) {
            inputStream = new FileInputStream(image);
        } else {
            inputStream = resourceLoader.getResource("classpath:/static/img/noImage.jpg").getInputStream();
        }

        String imageName = "thumbnail.jpg";
        String contentType = "image/jpg";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(imageName, StandardCharsets.UTF_8)
                        .build()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(inputStream);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }

    public ResponseEntity<Resource> checkResizeImage(int width) throws IOException {

        InputStream file = resourceLoader.getResource("classpath:/static/img/중고 자전거.jpg").getInputStream();

        String imageName = "중고 자전거.jpg";

        String contentType = "image/jpg";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(imageName, StandardCharsets.UTF_8)
                        .build()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(new FileInputStream(resizeImage(imageName, width, file)));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }

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

    // 게시글 검색
    public List<BoardEntity> getAllBoards() {
        return boardRepository.findAll();
    }

}
