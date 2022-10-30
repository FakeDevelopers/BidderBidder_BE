package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.domain.Constants;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        searchWord.trim(),
        1);
  }

  // 검색이 많은 검색어를 listCount 개수만큼 리스트로 받아옴
  public void getPopularSearchWord() {
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    List<String> rank = new ArrayList<>();
    Set<String> range = new HashSet<>();
    Map<String, Double> allWordsAndScore = new HashMap<>();
    Map<String, String> realWord = new HashMap<>();
    Map<String, Double> wordScore = new HashMap<>();

    // 이전 3일간의 모든 key 저장
    for (int i = 1; i <= Constants.SEARCH_WORD_REMAIN_3DAYS; i++) {
      range.addAll(getAllKeys(i));
    }

    for (String keyWord : range) {
      Set<String> wordSet = zSetOperations.range(keyWord, 0, -1);
      List<Double> scoreList =
          zSetOperations.score(keyWord, wordSet.toArray());

      int i = 0;
      // 모든 검색어와 해당 검색어의 횟수를 저장
      // 이미 저장했으면 횟수를 더함
      for (String word : wordSet) {
        allWordsAndScore.put(word, allWordsAndScore.getOrDefault(word, 0D) + scoreList.get(i));
        i++;
      }
    }

    // 모든 검색어를 횟수에 따라 오름차순 정렬
    List<String> allWordAndScoreKeySet = new ArrayList<>(allWordsAndScore.keySet());
    allWordAndScoreKeySet.sort(Comparator.comparing(allWordsAndScore::get));
    // 오름차순 정렬을 통해 밑에서 (공백 없는 단어, 원래 단어) 맵에 값을 추가할 때
    // 가장 횟수가 많은 원래 단어가 마지막으로 정렬되면서 마지막으로 realWord에 들어감

    // 공백 없는 단어, 원래 단어
    // 공백 없는 단어, 검색 횟수
    // 두 개의 맵을 만듦
    for (String key : allWordAndScoreKeySet) {
      String noSpaceKey = key.replace(" ", "");
      realWord.put(noSpaceKey, key);
      wordScore.put(noSpaceKey, wordScore.getOrDefault(noSpaceKey, 0D) + allWordsAndScore.get(key));
    }

    // 공백 없는 단어, 검색 횟수를 내림차순 정렬
    List<String> wordScoreKeySet = new ArrayList<>(wordScore.keySet());
    wordScoreKeySet.sort(
        (value1, value2) -> (wordScore.get(value2).compareTo(wordScore.get(value1))));

    // 내림차순으로 정렬된 맵을 이용하여 해당 키의 원래 단어를 rank에 저장
    for (String key : wordScoreKeySet) {
      rank.add(realWord.get(key));
    }

    yesterdaySearchRank = rank;
  }

  public List<String> getPopularSearchWord(int listCount) {
    return listCount < yesterdaySearchRank.size()
        ? yesterdaySearchRank.subList(0, listCount)
        : yesterdaySearchRank;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void deleteSearchWords() {
    getPopularSearchWord();
    redisTemplate.delete(getAllKeys(Constants.SEARCH_WORD_DELETE_BEFORE_4DAYS));
  }

  private Set<String> getAllKeys(int i) {
    return redisTemplate.keys(
        Constants.SEARCH_WORD_REDIS
            + LocalDateTime.now()
            .minusDays(i)
            .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
            + ":*");
  }
}
