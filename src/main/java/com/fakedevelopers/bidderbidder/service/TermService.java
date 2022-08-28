package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.exception.HttpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TermService {

	static private final String TERM_FOLDER = "./terms";
	static private final String TERM_EXTENSION = ".md";

	@Cacheable(value = "terms", key = "#termName")
	public String getTerm(String termName) {
		String filePath = TERM_FOLDER + File.separator + termName + TERM_EXTENSION;
		File term = new File(filePath);
		if (term.exists()) {
			try (InputStream inputStream = new FileInputStream(term)) {
				return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		throw new HttpException(HttpStatus.NOT_FOUND, "존재하지 않는 약관입니다.");
	}

	@CacheEvict(value = "terms", key = "#termName") // 약관이 업데이트 되면 해당 약관의 캐싱을 삭제합니다.
	public void addTerm(String termName, MultipartFile file) throws Exception {
		String filePath = TERM_FOLDER + File.separator + termName + TERM_EXTENSION;
		File term = new File(filePath);

		Files.createDirectories(Path.of(TERM_FOLDER));
		try (FileOutputStream outputStream = new FileOutputStream(term)) {
			outputStream.write(file.getBytes());
		}
	}

}
