package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.exception.UserNotFoundException;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import io.getstream.chat.java.models.User;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private final UserRepository userRepository;

  ChatService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String getToken(long id) {
    UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    return User.createToken(Long.toString(user.getId()), null, null);

  }
}
