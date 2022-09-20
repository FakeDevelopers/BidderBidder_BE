package com.fakedevelopers.bidderbidder.service;

import static com.fakedevelopers.bidderbidder.domain.Constants.OPTIONAL;
import static com.fakedevelopers.bidderbidder.domain.Constants.REQUIRED;

import com.fakedevelopers.bidderbidder.exception.TermNotFoundException;
import com.fakedevelopers.bidderbidder.model.TermEntity;
import com.fakedevelopers.bidderbidder.repository.TermRepository;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TermService {

  private static final String TERM_LIST = "termList";
  private static final String TERM = "term";

  private final TermRepository termRepository;
  private final CacheManager cacheManager;

  TermService(
      TermRepository termRepository, CacheManager cacheManager) {
    this.termRepository = termRepository;
    this.cacheManager = cacheManager;
  }

  @Cacheable(value = TERM_LIST)
  public Map<String, List<TermInfo>> getTerms() {
    List<TermInfo> requiredTerms = new ArrayList<>();
    List<TermInfo> optionalTerms = new ArrayList<>();
    Map<String, List<TermInfo>> map = new HashMap<>();

    termRepository.findAll().forEach(term -> {
      TermInfo termInfo = new TermInfo(term);
      if (term.getRequired()) {
        requiredTerms.add(termInfo);
      } else {
        optionalTerms.add(termInfo);
      }
    });
    map.put(REQUIRED, requiredTerms);
    map.put(OPTIONAL, optionalTerms);

    return map;
  }

  @Cacheable(value = TERM, key = "#id")
  public String getTerm(long id) {

    TermEntity term = termRepository.findById(id)
        .orElseThrow(TermNotFoundException::new);

    return term.getContents();
  }

  @Caching(evict = {
      @CacheEvict(value = TERM_LIST, allEntries = true) // termList라는 이름의 캐시를 지운다
  })
  @Transactional
  public void addTerm(String termName, boolean isRequired, MultipartFile file) throws Exception {
    TermEntity term = termRepository.findByName(termName);
    String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
    long id;

    if (term != null) {
      term.setContents(contents);
      term.setRequired(isRequired);
      id = term.getId();
    } else {
      id = termRepository.save(new TermEntity(0, termName, isRequired, contents)).getId();
    }

    Cache cache = cacheManager.getCache(TERM);
    if (cache != null) {
      cache.evict(id); // Annotation으로 처리하려고 했지만 파라메터로 들어오는게 아니라서 코드로 처리한다
    }
  }

  @Caching(evict = {
      @CacheEvict(value = TERM, key = "#id"), // term이라는 이름의 캐시의 #id값과
      @CacheEvict(value = TERM_LIST, allEntries = true) // termList이름의 캐시를 지운다
  })
  @Transactional
  public void deleteTerm(long id) {
    if (termRepository.findById(id).isEmpty()) {
      throw new TermNotFoundException();
    }

    termRepository.deleteById(id);
  }

  @Getter
  public static class TermInfo {

    private final long id;
    private final String name;

    private TermInfo(TermEntity term) {
      id = term.getId();
      name = term.getName();
    }
  }
}
