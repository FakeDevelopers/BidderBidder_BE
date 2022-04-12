package com.fakedevelopers.ddangddangmarket.service;

import com.fakedevelopers.ddangddangmarket.dto.BoardWriteDto;
import com.fakedevelopers.ddangddangmarket.model.BoardEntity;
import com.fakedevelopers.ddangddangmarket.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class BoardService {

    private final static String RESOURCE_PATH = "/resources/upload";
    private final BoardRepository boardRepository;
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
    public void compareBids(BoardWriteDto boardWriteDto) throws Exception {
        if (!boardWriteDto.getHope_bid().isBlank()) {
            if (Long.parseLong(boardWriteDto.getHope_bid()) < boardWriteDto.getOpening_bid()) {
                throw new Exception("시작가가 희망가보다 적어 ㅠㅠ ");
            }
        }
    }

    // 경매 마감 날짜가 지금 날짜보다 나중인지 확인
    public void compareDate(BoardWriteDto boardWriteDto) throws Exception {
        if (boardWriteDto.getEnd_date().isBefore(LocalDateTime.now())) {
            throw new Exception("마감 날짜가 지금 날짜보다 빨라요");
        }
    }

    // 파일 확장자 jpg, png, jpeg 아니면 안되게 했음 -> 확장자도 얘기 후 조정
    // 특정 확장자만 선택하게 하는건 프론트엔드에서도 가능
    // 백엔드에서도 확인하는게 좋으니 양쪽에서 특정 확장자를 맞춰야겠네요
    public void compareExtension(List<MultipartFile> files) throws Exception {
        for(MultipartFile file : files){
            String fileOriginName = file.getOriginalFilename();
            String extension = fileOriginName.split("\\.")[1];
            if(!(extension.equals("jpg") | extension.equals("png") | extension.equals("jpeg"))){
                throw new Exception("이미지 파일 아님");
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

    // 게시글 검색
    public List<BoardEntity> getAllBoards() {
        return boardRepository.findAll();
    }

}
