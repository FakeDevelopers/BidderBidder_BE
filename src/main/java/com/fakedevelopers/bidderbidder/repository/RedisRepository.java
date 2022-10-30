package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.domain.Constants;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
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
  public List<String> getPopularSearchWord() {
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    List<String> rank = new ArrayList<>();
    Set<String> range = new HashSet<>();
    Map<String, Double> allWordsAndScore = new HashMap<>();
    Map<String, String> realWord = new HashMap<>();
    Map<String, Double> wordScore = new HashMap<>();

    // 이전 3일간의 모든 key 저장
    for (int i = 1; i <= 3; i++) {
      range.addAll(redisTemplate.keys(
          Constants.SEARCH_WORD_REDIS
              + LocalDateTime.now()
              .minusDays(i)
              .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
              + ":*"));
    }

    if (range != null) {
      for (String keyWord : range) {
        Set<String> wordSet = zSetOperations.range(keyWord, 0, -1);
        List<Double> scoreList =
            zSetOperations.score(keyWord, zSetOperations.range(keyWord, 0, -1).toArray());

        int i=0;
        // 모든 검색어와 해당 검색어의 횟수를 저장
        // 이미 저장했으면 횟수를 더함
        for(String word : wordSet){
          if(!allWordsAndScore.containsKey(word)){
            allWordsAndScore.put(word, scoreList.get(i));
          }
          else{
            allWordsAndScore.replace(word, allWordsAndScore.get(word) + scoreList.get(i));
          }
          i++;
        }
      }
    }

    // 모든 검색어를 횟수에 따라 내림차순 정렬
    List<String> listTestKeySet = new ArrayList<>(allWordsAndScore.keySet());
    listTestKeySet.sort((value1, value2) -> (allWordsAndScore.get(value2).compareTo(allWordsAndScore.get(value1))));
    // 내림차순 정렬을 통해 밑에서 (공백 없는 단어, 원래 단어) 맵에 값을 추가할 때
    // 가장 횟수가 많은 원래 단어가 첫번 째로 들어감

    // 공백 없는 단어, 원래 단어
    // 공백 없는 단어, 검색 횟수
    // 두 개의 맵을 만듦
    for(String key : listTestKeySet){
      String noSpaceKey = key.replace(" ", "");
      if(!realWord.containsKey(noSpaceKey)){
        realWord.put(noSpaceKey, key);
        wordScore.put(noSpaceKey, allWordsAndScore.get(key));
      }
      else{
        wordScore.replace(noSpaceKey, wordScore.get(noSpaceKey) + allWordsAndScore.get(key));
      }
    }

    // 공백 없는 단어, 검색 횟수를 내림차순 정렬
    List<String> wordScoreKeySet = new ArrayList<>(wordScore.keySet());
    wordScoreKeySet.sort(
        (value1, value2) -> (wordScore.get(value2).compareTo(wordScore.get(value1))));

    // 내림차순으로 정렬된 맵을 이용하여 해당 키의 원래 단어를 rank에 저장
    for(String key : wordScoreKeySet){
      rank.add(realWord.get(key));
    }

    yesterdaySearchRank = rank;
    return rank;
  }

  public List<String> getPopularSearchWord(int listCount) {
    return listCount < yesterdaySearchRank.size()
        ? yesterdaySearchRank.subList(0, listCount)
        : yesterdaySearchRank;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void deleteSearchWords() {
    getPopularSearchWord();
    Set<String> words =
        redisTemplate.keys(
            Constants.SEARCH_WORD_REDIS
                + LocalDateTime.now()
                .minusDays(4)
                .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
                + ":*");
    redisTemplate.delete(words);
  }
}
