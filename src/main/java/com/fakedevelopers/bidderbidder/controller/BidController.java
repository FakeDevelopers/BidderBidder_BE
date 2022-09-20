package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.service.BidService;
import javax.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/{productId}/bid")
public class BidController {

  private final BidService bidService;

  BidController(BidService bidService) {
    this.bidService = bidService;
  }

  @PostMapping
  String addBid(@PathVariable long productId, @RequestParam long userId,
      @RequestParam @Min(0) long bid) {
    bidService.addBid(productId, userId, bid);
    return Constants.SUCCESS;
  }
}
