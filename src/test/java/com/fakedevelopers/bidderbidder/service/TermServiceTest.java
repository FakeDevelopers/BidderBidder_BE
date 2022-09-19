package com.fakedevelopers.bidderbidder.service;

import static com.fakedevelopers.bidderbidder.domain.Constants.OPTIONAL;
import static com.fakedevelopers.bidderbidder.domain.Constants.REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fakedevelopers.bidderbidder.IntegrationTestBase;
import com.fakedevelopers.bidderbidder.exception.TermNotFoundException;
import com.fakedevelopers.bidderbidder.model.TermEntity;
import com.fakedevelopers.bidderbidder.repository.TermRepository;
import com.fakedevelopers.bidderbidder.service.TermService.TermInfo;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("TermService 클래스")
class TermServiceTest extends IntegrationTestBase {

  private static TermEntity termEntity;
  @Autowired
  TermService sut;
  @Autowired
  TermRepository termRepository;

  @BeforeAll
  static void setUp(@Autowired TermRepository termRepository) {
    termEntity = termRepository.save(
        new TermEntity(0, "존재하지 않아야 하는 약관", false, "이 약관과 같은 이름의 약관은 만들지 않기를 바랍니다."));
  }

  @AfterAll
  static void tearDown(@Autowired TermRepository termRepository) {
    termRepository.deleteById(termEntity.getId());
  }

  @Nested
  @DisplayName("getTerms 메소드는")
  class Describe_getTerms {

    @Nested
    @DisplayName("항상")
    class Context_Always {

      @Test
      @DisplayName("성공한다")
      void it_success() {
        Map<String, List<TermInfo>> map = sut.getTerms();
        assertThat(map)
            .containsKey(REQUIRED)
            .containsKey(OPTIONAL);
      }
    }
  }

  @Nested
  @DisplayName("getTerm 메소드는")
  class Describe_getTerm {

    @Nested
    @DisplayName("존재하지 않는 ID의 약관을 요청하면")
    class Context_NotExistId {

      @Test
      @DisplayName("TermNotFoundException을 던진다")
      void it_throwTermNotFoundException() {
        assertThrows(TermNotFoundException.class, () -> sut.getTerm(-1));
      }
    }

    @Nested
    @DisplayName("존재하는 ID의 약관을 요청하면")
    class Context_ExistId {

      @Test
      @DisplayName("성공한다.")
      void it_success() {
        assertDoesNotThrow(() -> sut.getTerm(termEntity.getId()));
      }
    }
  }

  @Nested
  @DisplayName("addTerm 메소드는")
  class Describe_addTerm {

    @Nested
    @DisplayName("새로운 약관을 넣으면")
    class Context_NewTermName {

      @Test
      @DisplayName("성공한다.")
      void it_success() {
        String newTermName = "존재하지 않아야 하는 약관2";
        MultipartFile multipartFile = new MockMultipartFile("mock.txt",
            "mock.txt", "text/plain", "This is mocking.".getBytes(StandardCharsets.UTF_8));

        assertDoesNotThrow(() -> sut.addTerm(newTermName, true, multipartFile));

        TermEntity term = termRepository.findByName(newTermName);
        assertThat(term.getId()).isGreaterThan(
            termEntity.getId()); // 새로 생긴 엔티티는 기존 엔티티보다 큰 id 값을 가져야한다
      }
    }

    @Nested
    @DisplayName("존재하는 약관이름으로 요청하면")
    class Context_ExistTermName {

      @Test
      @DisplayName("업데이트에 성공한다.")
      void it_success() {
        String updateContents = "This is mocking.";
        MultipartFile multipartFile = new MockMultipartFile("mock.txt",
            "mock.txt", "text/plain", updateContents.getBytes(StandardCharsets.UTF_8));

        assertDoesNotThrow(() -> sut.addTerm(termEntity.getName(), true, multipartFile));
        TermEntity term = termRepository.getById(termEntity.getId());
        assertThat(term.getContents()).isEqualTo(updateContents);
        assertThat(term.getRequired()).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("deleteTerm 메소드는")
  class Describe_deleteTerm {

    @Nested
    @DisplayName("존재하지 않는 ID의 약관을 삭제 요청하면")
    class Context_NotExistId {

      @Test
      @DisplayName("TermNotFoundException을 던진다")
      void it_throwTermNotFoundException() {
        assertThrows(TermNotFoundException.class, () -> sut.deleteTerm(-1));
      }
    }

    @Nested
    @DisplayName("존재하는 ID의 약관을 삭제 요청하면")
    class Context_ExistTermId {

      @Test
      @DisplayName("성공한다.")
      void it_success() {
        assertDoesNotThrow(() -> sut.deleteTerm(termEntity.getId()));
      }
    }
  }
}
