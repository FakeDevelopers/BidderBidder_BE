package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.exception.HttpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TermService {

	private static final String TERM_FOLDER = "./terms" + File.separator;
	private static final String TERM_EXTENSION = ".md";
	private static final int TERM_EXTENSION_LENGTH = TERM_EXTENSION.length();
	private static final String REQUIRED = "required";
	private static final String OPTIONAL = "optional";
	private static final String TERM_LIST = "termList";
	private static final String TERM = "term";
	private static final String[] TERM_TYPES = {REQUIRED, OPTIONAL};


	@Cacheable(value = TERM_LIST)
	public Map<String, List<String>> getTerms() {
		Map<String, List<String>> map = new HashMap<>();
		for (String termType : TERM_TYPES) {
			List<String> termList = new ArrayList<>();
			Arrays.stream(Objects.requireNonNull(new File(TERM_FOLDER + termType).list()))
					.forEach(it -> termList.add(it.substring(0, it.length() - TERM_EXTENSION_LENGTH)));
			map.put(termType, termList);
		}
		return map;
	}

	@Cacheable(value = TERM, key = "#termName")
	public String getTerm(String termName) {

		for (String termType : TERM_TYPES) {
			File termFile = getTermFile(termType, termName);
			if (termFile.exists()) {
				try (InputStream inputStream = new FileInputStream(termFile)) {
					return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				} catch (Exception e) {
					throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			}
		}

		throw new HttpException(HttpStatus.NOT_FOUND, "존재하지 않는 약관입니다.");
	}


	@Caching(evict = {
			@CacheEvict(value = TERM, key = "#termName"), // term이라는 이름의 캐시의 #termName값과
			@CacheEvict(value = TERM_LIST, allEntries = true) //termList이름의 캐시를 지운다
	})
	public void addTerm(String termName, boolean isRequired, MultipartFile file) throws Exception {
		// 한개의 termType에 새로운 약관이 생겼을때 다른 타입의 약관에 이미 같은 이름의 약관이 존재 할수있으므로
		// 다른 타입의 약관에서 찾아서 지우려고 시도한다.
		File saveFile = getTermFile((isRequired ? REQUIRED : OPTIONAL), termName);
		File deleteFile = getTermFile((isRequired ? OPTIONAL : REQUIRED), termName);

		Files.createDirectories(Path.of(TERM_FOLDER + REQUIRED));
		Files.createDirectories(Path.of(TERM_FOLDER + OPTIONAL));
		try (FileOutputStream outputStream = new FileOutputStream(saveFile)) {
			outputStream.write(file.getBytes());
		}
		if (deleteFile.exists() && !deleteFile.delete()) {
			throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "파일삭제에 실패하였습니다.");
		}
	}



	@Caching(evict = {
			@CacheEvict(value = TERM, key = "#termName"), // term이라는 이름의 캐시의 #termName값과
			@CacheEvict(value = TERM_LIST, allEntries = true) //termList이름의 캐시를 지운다
  })
	public String deleteTerm(String termName) {
		Map<String, List<String>> termListMap = getTerms(); // getTerms는 캐싱되어있으니 파일시스템에서 리스트를 가져오는것보다 빠를것이다

		for (String termType : TERM_TYPES) { // 필수약관 한번, 선택 약관 한번 아래의 코드를 실행한다.
			if (termListMap.get(termType).contains(termName) && getTermFile(termType, termName).delete()) {
				return Constants.SUCCESS;
			}
		}
		return Constants.FAIL;
	}

	private File getTermFile(String termType, String termName) {
		String termFilePath = TERM_FOLDER + termType + File.separator + termName + TERM_EXTENSION;
		return new File(termFilePath);
	}
}
