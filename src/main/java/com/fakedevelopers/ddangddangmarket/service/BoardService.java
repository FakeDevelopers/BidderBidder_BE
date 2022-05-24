package com.fakedevelopers.ddangddangmarket.service;

import com.fakedevelopers.ddangddangmarket.domain.Constants;
import com.fakedevelopers.ddangddangmarket.dto.BoardWriteDto;
import com.fakedevelopers.ddangddangmarket.dto.ProductListDto;
import com.fakedevelopers.ddangddangmarket.dto.ProductListRequestDto;
import com.fakedevelopers.ddangddangmarket.exception.InvalidExpirationDateException;
import com.fakedevelopers.ddangddangmarket.exception.InvalidExtensionException;
import com.fakedevelopers.ddangddangmarket.exception.InvalidHopePriceException;
import com.fakedevelopers.ddangddangmarket.model.BoardEntity;
import com.fakedevelopers.ddangddangmarket.repository.BoardRepository;
import imageUtil.Image;
import imageUtil.ImageLoader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
public class BoardService {

    private static final String RESOURCE_PATH = "/resources/upload";
    private final ResourceLoader resourceLoader;
    private final BoardRepository boardRepository;
    private final String[] extensions = {"jpg", "jpeg", "png"};
    private final String[] productLists = {"원격 방망이", "모기향", "이어폰", "냄비", "베개",
            "마우스", "후드티", "의자", "볼펜", "쓰레기통"};
    private final ArrayList<String> extensionList = new ArrayList<>(Arrays.asList(extensions));
    ServletContext servletContext;

    BoardService(BoardRepository boardRepository, ServletContext servletContext, ResourceLoader resourceLoader) {
        this.boardRepository = boardRepository;
        this.servletContext = servletContext;
        this.resourceLoader = resourceLoader;
    }

    // 게시글 저장
    public void saveBoard(BoardWriteDto boardWriteDto, List<MultipartFile> files) throws Exception {
        compareBids(boardWriteDto);
        compareDate(boardWriteDto);
        compareExtension(files);
        String path = createPathIfNeed();
        BoardEntity boardEntity = new BoardEntity(path, boardWriteDto, files);

        boardRepository.save(boardEntity);

    }

    // 시작가가 희망가보다 적은지 확인
    private void compareBids(BoardWriteDto boardWriteDto) {
        if (boardWriteDto.getHope_price() != null) {
            if (boardWriteDto.getHope_price() < boardWriteDto.getOpening_bid()) {
                throw new InvalidHopePriceException("희망가가 시작가보다 적어 ㅠㅠ ");
            }
        }
    }

    // 경매 마감 날짜가 지금 날짜보다 나중인지 확인
    private void compareDate(BoardWriteDto boardWriteDto) {
        if (boardWriteDto.getEnd_date().isBefore(LocalDateTime.now())) {
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

    // 파일 경로 생성
    private String createPathIfNeed() throws IOException {
        String realPath = servletContext.getRealPath(RESOURCE_PATH);
        String today = new SimpleDateFormat("yyMMdd").format(new Date());
        String path = realPath + File.separator + today;
        Files.createDirectories(Path.of(path));
        return path;
    }

    public List<ProductListDto> createPageProductLists(ProductListRequestDto productListRequestDto, int page) {
        List<ProductListDto> productList;

        int size = productListRequestDto.getListCount();
        int nextNumber = (page - 1) * size;

        size = min(size, max(0, Constants.ITEMMAXCOUNT - nextNumber));

        productList = makeProductList(size, Constants.ITEMMAXCOUNT - nextNumber);

        return productList;
    }

    public List<ProductListDto> createInfiniteProductLists(ProductListRequestDto productListRequestDto, int startNumber) {
        List<ProductListDto> productList;

        int size = productListRequestDto.getListCount();

        if (startNumber == -1) {
            startNumber = Constants.ITEMMAXCOUNT;
        }

        size = (startNumber < size) ? startNumber : min(size, max(0, Constants.ITEMMAXCOUNT - startNumber + size));

        productList = makeProductList(size, startNumber);

        return productList;
    }

    private List<ProductListDto> makeProductList(int size, int boardId) {
        List<ProductListDto> productListDtos = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int i = 1; i <= size; i++) {
            SecureRandom random = new SecureRandom();
            int randomZeroNine = random.nextInt(10);
            int randomOneTen = random.nextInt(10) + 1;
            long curTime = System.currentTimeMillis() + random.nextInt((9 + 2 * 24) * 60 * 60 * 1000);
            productListDtos.add(new ProductListDto(boardId - i, "https://pbs.twimg.com/profile_images/738200195578494976/CuZ9yUAT_400x400.jpg",
                    productLists[randomZeroNine], randomOneTen * 10000L, randomZeroNine * 1000,
                    randomOneTen * 300, format.format(new Date(curTime)), randomZeroNine * 5));
        }
        return productListDtos;
    }

    public ResponseEntity<Resource> imageResize(int width) throws IOException {

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

    private File resizeImage(String imageName, int width, InputStream file) throws IOException {
        Image img = ImageLoader.fromStream(file);
        Image resizedImg = img.getResizedToSquare(width, 0.0);

        return resizedImg.writeToFile(new File("resize_" + imageName));
    }

    // 게시글 검색
    public List<BoardEntity> getAllBoards() {
        return boardRepository.findAll();
    }

}
