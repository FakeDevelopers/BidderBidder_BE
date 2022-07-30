package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.domain.Constants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;
  private List<String> yesterdaySearchRank = new ArrayList<>();

  RedisRepository(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // 검색어 레디스에 저장 및 score 올리기
  public void saveSearchWord(String searchWord) {
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    String noSpaceWord = searchWord.replace(" ", "");
    zSetOperations.incrementScore(
        Constants.SEARCH_WORD_REDIS
            + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
            + ":"
            + noSpaceWord,
        searchWord,
        1);
  }

  // 검색이 많은 검색어를 listCount 개수만큼 리스트로 받아옴
  public List<String> getPopularSearchWord() {
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    Double sum;
    List<String> rank = new ArrayList<>();
    Set<Pair<String, Double>> keySet =
        new TreeSet<>(
            (wordAndScore1, wordAndScore2) -> {
              if (BigDecimal.valueOf(wordAndScore2.getSecond())
                      .compareTo(BigDecimal.valueOf(wordAndScore1.getSecond()))
                  == 0) {
                return wordAndScore2.getFirst().equals(wordAndScore1.getFirst())
                    ? 1
                    : wordAndScore2.getFirst().compareTo(wordAndScore1.getFirst());
              }
              return (int) (wordAndScore2.getSecond() - wordAndScore1.getSecond());
            });
    Set<String> range =
        redisTemplate.keys(
            Constants.SEARCH_WORD_REDIS
                + LocalDateTime.now()
                    .minusMinutes(1)
                    .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
                + ":*");

    if (range != null) {
      for (String keyWord : range) {
        List<Double> scoreList =
            zSetOperations.score(keyWord, zSetOperations.range(keyWord, 0, -1).toArray());
        sum = scoreList.stream().mapToDouble(Double::doubleValue).sum();

        keySet.add(Pair.of(keyWord, sum));
      }
    }

    for (Pair<String, Double> key : keySet) {
      String tmp = zSetOperations.reverseRange(key.getFirst(), 0, 0).toString();
      rank.add(tmp.substring(1, tmp.length() - 1));
    }
    return rank;
  }

  public List<String> getPopularSearchWord(int listCount) {
    return listCount < yesterdaySearchRank.size()
        ? yesterdaySearchRank.subList(0, listCount)
        : yesterdaySearchRank;
  }

  @Scheduled(cron = "0 * * * * *")
  public void getAndDeleteSearchWords() {
    yesterdaySearchRank = getPopularSearchWord();
    Set<String> words =
        redisTemplate.keys(
            Constants.SEARCH_WORD_REDIS
                + LocalDateTime.now()
                    .minusMinutes(1)
                    .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
                + ":*");
    redisTemplate.delete(words);
  }
}
