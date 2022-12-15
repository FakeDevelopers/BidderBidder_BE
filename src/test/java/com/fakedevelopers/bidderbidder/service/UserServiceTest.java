package com.fakedevelopers.bidderbidder.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DisplayName("UserService 클래스")
public class UserServiceTest extends IntegrationTestBase {

  @Autowired
  public UserRepository userRepository;
  @Autowired
  public UserService userService;


  public static Stream<Arguments> validRegisterDtoGenerator() {
    ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
    return Stream.of(Arguments.of("validUser" + randomGenerator.nextInt(1, 1000), "a@b.com",
            Constants.INIT_NICKNAME), // 기본 닉네임
        Arguments.of("validUser" + randomGenerator.nextInt(1, 1000), "a@b.com",
            "mockup"));
  }

  public static Stream<Arguments> invalidRegisterDtoGenerator() {
    ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
    return Stream.of(
        // username 이 6글자 미만인 경우 - 1 (empty)
        Arguments.of("", "a@b.com", Constants.INIT_NICKNAME),

        // username이 6글자 미만인 경우 - 2
        Arguments.of("aaa", "a@b.com", Constants.INIT_NICKNAME),

        // username이 64글자를 초과
        Arguments.of("invalidUser".repeat(30), "a@b.com", Constants.INIT_NICKNAME),

        // 이메일 형식에 맞지 않는 정보가 들어왔을 때
        Arguments.of("invalidUser" + randomGenerator.nextInt(1, 1000), "invalidEmail",
            Constants.INIT_NICKNAME),

        // nickname이 3글자 미만인 경우 - 1 (Empty)
        Arguments.of("invalidUser" + randomGenerator.nextInt(1, 1000), "a@b.com", ""),

        // nickname이 3글자 미만인 경우 - 2
        Arguments.of("invalidUser" + randomGenerator.nextInt(1, 1000), "a@b.com", "a"),

        // nickname이 12글자 초과인 경우
        Arguments.of("invalidUser" + randomGenerator.nextInt(1, 1000), "a@b.com", "a".repeat(20)));
  }


  @Nested
  @DisplayName("Register 메소드는")
  class Describe_Register {

    @Nested
    @DisplayName("정당한 OAuth2RegisterDto에 대해")
    class Context_Validate_Input {

      @ParameterizedTest
      @MethodSource(value = "com.fakedevelopers.bidderbidder.service.UserServiceTest#validRegisterDtoGenerator")
      @DisplayName("올바른 UserEntity를 반환한다")
      void returns_valid_UserEntity(String username, String email, String nickname) {
        UserEntity result = userService.register(
            OAuth2UserRegisterDto.builder().username(username).email(email).nickname(nickname)
                .build());
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).startsWith(nickname);
      }
    }

    @Nested
    @DisplayName("정당하지 못한 OAuth2RegisterDto에 대해")
    class Context_Invalid_Input {

      @ParameterizedTest
      @MethodSource(value = "com.fakedevelopers.bidderbidder.service.UserServiceTest#invalidRegisterDtoGenerator")
      @DisplayName("저장하지 않는다")
      void Do_Not_Save(String username, String email, String nickname) {
        OAuth2UserRegisterDto dto = OAuth2UserRegisterDto.builder().username(username).email(email)
            .nickname(nickname).build();

        Assertions.assertThrows(Exception.class, () -> userService.register(dto));
      }
    }
  }

}
