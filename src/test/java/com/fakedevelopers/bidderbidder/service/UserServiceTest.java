package com.fakedevelopers.bidderbidder.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.dto.UserLoginDto;
import com.fakedevelopers.bidderbidder.dto.UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DisplayName("UserService 클래스의")
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

  public static Stream<Arguments> invalidLoginDtoGenerator() {
    return Stream.of(
        Arguments.of("test1", "wrong123"),
        Arguments.of("test2", "wrong123"),
        Arguments.of("test3", "wrong123"),
        Arguments.of("test4", "wrong123"),
        Arguments.of("test5", "wrong123"),
        Arguments.of("test6", "wrong123"),
        Arguments.of("test7", "wrong123"),
        Arguments.of("test8", "wrong123"),
        Arguments.of("test9", "wrong123"),
        Arguments.of("test10", "wrong123")
    );
  }

  public static Stream<Arguments> validLoginDtoGenerator() {
    return Stream.of(
        Arguments.of("test1", "123"),
        Arguments.of("test2", "123"),
        Arguments.of("test3", "123"),
        Arguments.of("test4", "123"),
        Arguments.of("test5", "123"),
        Arguments.of("test6", "123"),
        Arguments.of("test7", "123"),
        Arguments.of("test8", "123"),
        Arguments.of("test9", "123"),
        Arguments.of("test10", "123")
    );
  }

  @Nested
  @DisplayName("Register 메소드는")
  class Describe_Register {

    private final Random random = new Random();
    private final String randomPassword = random.ints(97, 123)
        .limit(12)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    @Test
    void randomPasswordGenerateTest() {
      assertThat(randomPassword).isNotNull();
      assertThat(randomPassword.length()).isBetween(Constants.MIN_PASSWORD_SIZE,
          Constants.MAX_PASSWORD_SIZE);
      Logger logger = LoggerFactory.getLogger(this.getClass());
      logger.info("Generated Password -> " + randomPassword);
    }


    @Nested
    @DisplayName("정당한 신규 회원에 대해")
    class Context_register_new_member {

      @ParameterizedTest
      @MethodSource(value = "com.fakedevelopers.bidderbidder.service.UserServiceTest#validRegisterDtoGenerator")
      @DisplayName("파이어베이스 토큰 값을 반환한다.")
      void It_returns_firebase_custom_token(String username, String email, String nickname)
          throws FirebaseAuthException {
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .username(username)
            .email(email)
            .nickname(nickname)
            .password(randomPassword)
            .build();

        String firebaseCustomToken = userService.register(userRegisterDto);
        assertThat(firebaseCustomToken).isInstanceOf(String.class);
        // 테스트가 성공한다면, firebase에 방금 저장된 테스트 유저정보를 삭제합니다
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        try {
          firebaseAuth.deleteUser(userRegisterDto.getUsername());
        } catch (FirebaseAuthException e) {
          throw new RuntimeException(
              "Cannot delete Firebase user! ==> uid: " + userRegisterDto.getUsername());
        }
      }
    }

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

  @Nested
  @DisplayName("Login 메소드는")
  class Describe_Login {

    @Nested
    @DisplayName("존재하는 회원에 대해서")
    class Context_Existing_User {

      @Nested
      @DisplayName("올바르지 않는 패스워드가 들어오면")
      class Context_Incorrect_Password {

        @ParameterizedTest
        @MethodSource("com.fakedevelopers.bidderbidder.service.UserServiceTest#invalidLoginDtoGenerator")
        @DisplayName("예외를 던진다")
        void It_returns_exception(String username, String password) {
          UserLoginDto dto = new UserLoginDto(username, password);
          Assertions.assertThrows(Exception.class, () -> userService.userLoginWithPassword(dto));
        }
      }

      @Nested
      @DisplayName("올바른 패스워드가 들어오면")
      class Context_Correct_Password {

        @ParameterizedTest
        @MethodSource("com.fakedevelopers.bidderbidder.service.UserServiceTest#validLoginDtoGenerator")
        @DisplayName("유저 정보를 반환한다")
        void It_returns_userInfo(String username, String password) {
          UserLoginDto dto = new UserLoginDto(username, password);
          assertThat(userService.userLoginWithPassword(dto).getEmail()).isNotNull();
          assertThat(userService.userLoginWithPassword(dto).getUsername()).isEqualTo(username);
          assertThat(userService.userLoginWithPassword(dto).getNickname()).isNotNull();
        }
      }
    }
  }

  @Nested
  @DisplayName("GetUser 메소드는")
  class Describe_GetUser {

    @Nested
    @DisplayName("존재하지 않는 회원 아이디에 대하여")
    class Context_Invalid_Username {

      @Test
      @DisplayName("예외를 던진다")
      void It_Throws_UserNotFoundException() {
        String invalidUsername = "NOT_FOUND";
        Assertions.assertThrows(UsernameNotFoundException.class,
            () -> userService.getUser(invalidUsername));
      }
    }

    @Nested
    @DisplayName("존재하는 회원 아이디에 대하여")
    class Context_Valid_Username {

      @ParameterizedTest
      @ValueSource(strings = {"test1", "test2", "test3", "test4", "test5", "test6", "test7",
          "test8", "test9", "test10"})
      @DisplayName("UserInfo를 반환한다")
      void It_Returns_UserInfo(String username) {
        assertThat(userService.getUser(username)).isNotNull();
        assertThat(userService.getUser(username).getUsername()).isEqualTo(username);
      }
    }
  }
}
