package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.PageListResponseDto;
import com.fakedevelopers.bidderbidder.dto.ProductInformationDto;
import com.fakedevelopers.bidderbidder.dto.ProductListDto;
import com.fakedevelopers.bidderbidder.dto.ProductListRequestDto;
import com.fakedevelopers.bidderbidder.dto.ProductUpsertDto;
import com.fakedevelopers.bidderbidder.model.CategoryEntity;
import com.fakedevelopers.bidderbidder.model.ProductEntity;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.service.ProductService;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product")
public class ProductController {

  private final ProductService productService;

  ProductController(ProductService productService) {
    this.productService = productService;
  }

  // 게시글 작성
  @PostMapping(
      value = "/write",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  String productWrite(UserEntity userEntity,
      @Validated ProductUpsertDto productUpsertDto,
      @RequestPart(required = false) List<MultipartFile> files)
      throws Exception {
    productService.saveProduct(userEntity, productUpsertDto, files);
    return Constants.SUCCESS;
  }

  @GetMapping("/getPageProductList")
  PageListResponseDto getPageProductList(
      ProductListRequestDto productListRequestDto,
      @RequestParam(required = false, defaultValue = "1") int page) {

    return productService.getProductListPagination(productListRequestDto, page);
  }

  @GetMapping("/getInfiniteProductList")
  List<ProductListDto> getInfiniteProductList(
      ProductListRequestDto productListRequestDto,
      @RequestParam(required = false, defaultValue = "-1") int startNumber) {

    return productService.getProductListInfiniteScroll(productListRequestDto, startNumber);
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
  ResponseEntity<ProductInformationDto> getProductInfo(
      @PathVariable long productId) {
    return productService.getProductInfo(productId);
  }

  @PutMapping(
      value = "/modifyProductInfo/{productId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  String modifyProductInfo(
      UserEntity userEntity,
      @Validated ProductUpsertDto productUpsertDto,
      @RequestPart(required = false) List<MultipartFile> files,
      @PathVariable long productId)
      throws Exception {
    productService.modifyProduct(userEntity, productUpsertDto, files, productId);
    return Constants.SUCCESS;
  }

  @DeleteMapping("/deleteProduct/{productId}")
  String deleteProduct(UserEntity userEntity, @PathVariable long productId) throws IOException {
    productService.deleteProduct(userEntity, productId);
    return Constants.SUCCESS;
  }

  @GetMapping("/getImage/{imageId}")
  ResponseEntity<Resource> getProductImage(@PathVariable long imageId) throws IOException {
    return productService.getProductImage(imageId);
  }

  @GetMapping("/getSearchRank")
  List<String> searchRankList(int listCount) {
    return productService.getPopularSearchWord(listCount);
  }

  @GetMapping("/getAllCategory")
  List<CategoryEntity> getCategories() {
    return productService.getCategoryLevel1();
  }

  @PostMapping("/addCategory")
  void addCategory(String cateName, Long parentCateId) {
    productService.addCategory(cateName, parentCateId);
  }

  // 게시글 전체 찾기
  @GetMapping("/getAll")
  List<ProductEntity> getAllProducts() {
    return productService.getAllProducts();
  }
}
