package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.domain.Constants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class RedisRepository {

    StringRedisTemplate redisTemplate;
    private List<String> yesterdaySearchRank = new ArrayList<>();

    RedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 검색어 레디스에 저장 및 score 올리기
    public void saveSearchWord(String searchWord) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String noSpaceWord = searchWord.replace(" ", "");
        zSetOperations.incrementScore("searchWord:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS))
                + ":" + noSpaceWord, searchWord, 1);
    }

    // 검색이 많은 검색어를 listCount 개수만큼 리스트로 받아옴
    public List<String> getPopularSearchWord() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Double sum;
        ArrayList<String> rank = new ArrayList<>();
        SortedSet<Pair<String, Double>> keySet = new TreeSet<>((o1, o2) -> {
            if (o2.getSecond() - o1.getSecond() == 0) {
                if (o2.getFirst().equals(o1.getFirst()))
                    return 1;
                else {
                    return o2.getFirst().compareTo(o1.getFirst());
                }
            }
            return (int) (o2.getSecond() - o1.getSecond());
        });
        Set<String> range = redisTemplate.keys("searchWord:" +
                LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS)) + ":*");

        if (range != null) {
            for (String keyWord : range) {
                sum = 0d;
                for (String valueWord : zSetOperations.range(keyWord, 0, -1)) {
                    sum += zSetOperations.score(keyWord, valueWord);
                }
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
        if (listCount < yesterdaySearchRank.size()) {
            System.out.println(yesterdaySearchRank);
            return yesterdaySearchRank.subList(0, listCount);
        } else {
            System.out.println(yesterdaySearchRank);
            return yesterdaySearchRank;
        }
    }

    public void deleteSearchWords() {
        yesterdaySearchRank = getPopularSearchWord();
        System.out.println(yesterdaySearchRank);
        Set<String> words = redisTemplate.keys("searchWord:" +
                LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_REDIS)) + ":*");
        redisTemplate.delete(words);
    }

}
