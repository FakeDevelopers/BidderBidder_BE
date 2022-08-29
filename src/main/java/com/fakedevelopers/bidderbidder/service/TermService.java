package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.exception.HttpException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
	private static final String[] TERM_TYPES = {REQUIRED, OPTIONAL};

	public JsonObject getTerms() {
		JsonObject jsonObject = new JsonObject();

		for (String termType : TERM_TYPES) {
			JsonArray termList = new JsonArray();
			Arrays.stream(Objects.requireNonNull(
							new File(TERM_FOLDER + termType).list()))
					.forEach(
							it -> termList.add(it.substring(0, it.length() - TERM_EXTENSION_LENGTH)));

			jsonObject.add(termType, termList);
		}
		return jsonObject;
	}

	@Cacheable(value = "terms", key = "#termName")
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

	@CacheEvict(value = "terms", key = "#termName") // 약관이 업데이트 되면 해당 약관의 캐싱을 삭제합니다.
	public void addTerm(String termName, Boolean isRequired, MultipartFile file) throws Exception {
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

	@CacheEvict(value = "terms", key = "#termName")
	public boolean deleteTerm(String termName) {

		for (String termType : TERM_TYPES) {
			File termFile = getTermFile(termType, termName);
			if (termFile.exists() && termFile.delete()) {
				return true;
			}
		}
		return false;
	}

	private File getTermFile(String termType, String termName) {
		String termFilePath =
				TERM_FOLDER + termType + File.separator + termName + TERM_EXTENSION;
		return new File(termFilePath);
	}
}
