package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.service.BidService;
import javax.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/{id}/bid")
public class BidController {

  private final BidService bidService;

  BidController(BidService service) {
    bidService = service;
  }

  @PostMapping
  String addBid(@PathVariable long id, long userId, @Min(0) long bid) {
    bidService.addBid(id, userId, bid);
    return "success";
  }
}
