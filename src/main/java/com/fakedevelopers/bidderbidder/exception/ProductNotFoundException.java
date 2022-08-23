package com.fakedevelopers.bidderbidder.exception;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(long id) {
    super("product not found productId : " + id);
  }
}
