package com.fakedevelopers.ddangddangmarket.controller;

import com.fakedevelopers.ddangddangmarket.dto.InfiniteProductListRequestDto;
import com.fakedevelopers.ddangddangmarket.dto.PageProductListRequestDto;
import com.fakedevelopers.ddangddangmarket.dto.ProductListDto;
import com.fakedevelopers.ddangddangmarket.dto.BoardWriteDto;
import com.fakedevelopers.ddangddangmarket.model.BoardEntity;
import com.fakedevelopers.ddangddangmarket.service.BoardService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 작성
    @PostMapping(value = "/write", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String boardWrite(@Validated BoardWriteDto boardWriteDto,
                      @RequestPart(required = false) List<MultipartFile> files) throws Exception {
        boardService.saveBoard(boardWriteDto, files);
        return "success";
    }

    @GetMapping("/getPageProductList")
    List<ProductListDto> getProductList(PageProductListRequestDto pageProductListRequestDto) {

        return boardService.createProductLists(pageProductListRequestDto);
    }

    @GetMapping("/getInfiniteProductList")
    List<ProductListDto> getProductList(InfiniteProductListRequestDto infiniteProductListRequestDto){

        return boardService.createProductLists(infiniteProductListRequestDto);
    }

    // 게시글 전체 찾기
    @GetMapping("/getAll")
    List<BoardEntity> getAllBoards() {
        return boardService.getAllBoards();
    }

}
