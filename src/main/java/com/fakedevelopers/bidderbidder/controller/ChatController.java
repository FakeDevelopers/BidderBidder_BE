package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

  private final ChatService chatService;

  ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @GetMapping("/token/{id}")
  String getToken(long id) {
    return chatService.getToken(id);
  }

}
