package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.dto.PageListResponseDto;
import com.fakedevelopers.bidderbidder.dto.ProductInformationDto;
import com.fakedevelopers.bidderbidder.dto.ProductListDto;
import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.dto.ProductWriteDto;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 게시글 작성
    @PostMapping(value = "/write", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String productWrite(@Validated ProductWriteDto productWriteDto,
                        @RequestPart(required = false) List<MultipartFile> files) throws Exception {
        productService.saveProduct(productWriteDto, files);
        return "success";
    }

    @GetMapping("/getPageProductList")
    PageListResponseDto getPageProductList(ProductListRequestDto productListRequestDto,
                                           @RequestParam(required = false, defaultValue = "1") int page) {

        return productService.makePageListResponseDto(productListRequestDto, page);
    }

    @GetMapping("/getInfiniteProductList")
    List<ProductListDto> getInfiniteProductList(ProductListRequestDto productListRequestDto,
                                                @RequestParam(required = false, defaultValue = "-1") int startNumber) {

        return productService.createInfiniteProductLists(productListRequestDto, startNumber);
    }

    @GetMapping("/checkResizedImage")
    ResponseEntity<Resource> getResizedImage(int width) throws IOException {
        return productService.checkResizeImage(width);
    }

    @GetMapping("/getThumbnail")
    ResponseEntity<Resource> getThumbnail(Long productId, Boolean isWeb) throws IOException {
        return productService.getThumbnail(productId, isWeb);
    }

    @GetMapping("/getProductInfo/{productId}")
    ResponseEntity<ProductInformationDto> getProductInfo(@PathVariable long productId) {
        return productService.getProductInfo(productId);
    }

    @GetMapping("/getImage/{imageId}")
    ResponseEntity<Resource> getProductImage(@PathVariable long imageId) throws IOException {
        return productService.getProductImage(imageId);
    }

    @PostMapping("/saveSearchWord")
    String saveSearchWord(String searchWord) {
        return productService.saveSearchWord(searchWord);
    }

    @GetMapping("/getSearchRank")
    List<String> searchRankList(int listCount) {
        return productService.getPopularSearchWord(listCount);
    }

    // 게시글 전체 찾기
    @GetMapping("/getAll")
    List<ProductEntity> getAllProducts() {
        return productService.getAllProducts();
    }
}
