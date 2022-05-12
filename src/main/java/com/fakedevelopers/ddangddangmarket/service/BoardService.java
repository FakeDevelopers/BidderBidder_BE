package com.fakedevelopers.ddangddangmarket.service;

import com.fakedevelopers.ddangddangmarket.dto.BoardWriteDto;
import com.fakedevelopers.ddangddangmarket.dto.InfiniteProductListRequestDto;
import com.fakedevelopers.ddangddangmarket.dto.PageProductListRequestDto;
import com.fakedevelopers.ddangddangmarket.dto.ProductListDto;
import com.fakedevelopers.ddangddangmarket.exception.InvalidExpirationDateException;
import com.fakedevelopers.ddangddangmarket.exception.InvalidExtensionException;
import com.fakedevelopers.ddangddangmarket.exception.InvalidHopePrice;
import com.fakedevelopers.ddangddangmarket.model.BoardEntity;
import com.fakedevelopers.ddangddangmarket.repository.BoardRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
public class BoardService {

    private final static String RESOURCE_PATH = "/resources/upload";
    private final BoardRepository boardRepository;
    private final String[] extensions = {"jpg", "jpeg", "png"};
    private final String[] productLists = {"원격 방망이", "모기향", "이어폰", "냄비", "베개",
            "마우스", "후드티", "의자", "볼펜", "쓰레기통"};
    private final ArrayList<String> extensionList = new ArrayList<>(Arrays.asList(extensions));

    private final int MAX_NUMBER = 10000;

    ServletContext servletContext;

    BoardService(BoardRepository boardRepository, ServletContext servletContext) {
        this.boardRepository = boardRepository;
        this.servletContext = servletContext;
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
    public void compareBids(BoardWriteDto boardWriteDto) {
        if (boardWriteDto.getHope_price() != null) {
            if (boardWriteDto.getHope_price() < boardWriteDto.getOpening_bid()) {
                throw new InvalidHopePrice("희망가가 시작가보다 적어 ㅠㅠ ");
            }
        }
    }

    // 경매 마감 날짜가 지금 날짜보다 나중인지 확인
    public void compareDate(BoardWriteDto boardWriteDto) {
        if (boardWriteDto.getEnd_date().isBefore(LocalDateTime.now())) {
            throw new InvalidExpirationDateException("마감 날짜가 지금 날짜보다 빨라요");
        }
    }

    // 파일 확장자 jpg, png, jpeg 아니면 안되게 했음 -> 확장자도 얘기 후 조정
    // 특정 확장자만 선택하게 하는건 프론트엔드에서도 가능
    // 백엔드에서도 확인하는게 좋으니 양쪽에서 특정 확장자를 맞춰야겠네요
    public void compareExtension(List<MultipartFile> files) {
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

    public List<ProductListDto> createProductLists(PageProductListRequestDto pageProductListRequestDto) {
        List<ProductListDto> boardListDto = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal.add(Calendar.DATE, 3);

        int size = pageProductListRequestDto.getListCount();
        int nextNumber = (pageProductListRequestDto.getPage()-1) * size;

        size = min(size, max(0,MAX_NUMBER-nextNumber));

        for (int i = 0; i < size; i++) {
            Random random = new Random();
            int randomZeroNine = random.nextInt(10);
            int randomOneTen = random.nextInt(10) + 1;
            boardListDto.add(new ProductListDto(MAX_NUMBER-i-nextNumber, "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",
                    productLists[randomZeroNine], randomOneTen * 10000L, randomZeroNine * 1000,
                    randomOneTen * 300, format.format(cal.getTime()), randomZeroNine * 5));
        }
        return boardListDto;
    }

    public List<ProductListDto> createProductLists(InfiniteProductListRequestDto infiniteProductListRequestDto) {
        List<ProductListDto> boardListDto = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal.add(Calendar.DATE, 3);

        int size = infiniteProductListRequestDto.getListCount();
        int startNumber = infiniteProductListRequestDto.getStartNumber();

        if (startNumber == -1) {
            startNumber = MAX_NUMBER;
        }

        if(startNumber<size) {
            size = startNumber;
        }
        else {
            size = min(size, max(0, MAX_NUMBER - startNumber + size));
        }

        for(int i=1; i <= size; i++){
            Random random = new Random();
            int randomZeroNine = random.nextInt(10);
            int randomOneTen = random.nextInt(10) + 1;
            boardListDto.add(new ProductListDto(startNumber-i, "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",
                    productLists[randomZeroNine], randomOneTen * 10000L, randomZeroNine * 1000,
                    randomOneTen * 300, format.format(cal.getTime()), randomZeroNine * 5));
        }
        return boardListDto;
    }

    // 게시글 검색
    public List<BoardEntity> getAllBoards() {
        return boardRepository.findAll();
    }

}
