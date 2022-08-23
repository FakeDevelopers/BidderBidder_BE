package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends HttpException {

  public ProductNotFoundException(long id) {
    super(HttpStatus.NOT_FOUND, "product not found productId : " + id);
  }
}
