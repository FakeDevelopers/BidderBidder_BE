package com.fakedevelopers.bidderbidder.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureTestDatabase(replace = Replace.NONE)
class OAuth2UserServiceTest extends IntegrationTestBase {

  @Autowired
  private UserRepository userRepository;

  @Test
  void commitTest() {
    UserEntity user = UserEntity.builder()
        .username("google_aJcZ")
        .email("hello@abc.com")
        .nickname("bidder123231")
        .build();
    assertDoesNotThrow(() -> {
      userRepository.save(user);
    });
  }

  @Test
  void googleSigninTest() {
    UserEntity user = UserEntity.builder()
        .username("google_admin")
        .email("testjoon@a.com")
        .nickname("bidder102051")
        .build();
    assertDoesNotThrow(() -> {
      userRepository.save(user);
    });
  }
}
