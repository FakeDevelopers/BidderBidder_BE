package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.service.TermService;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/term")
public class TermController {

	private final TermService termService;

	TermController(TermService termService) {
		this.termService = termService;
	}

	@GetMapping("/list/")
	Map<String, List<String>> getTerms() {
		return termService.getTerms();
	}

	@GetMapping("/{termName}")
	String getTerm(@PathVariable String termName) {
		return termService.getTerm(termName);
	}

	@PostMapping(value = "/{termName}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	String addTerm(@PathVariable String termName, @RequestPart MultipartFile term,
			@RequestParam boolean isRequired)
			throws Exception { // 나중에 요 API는 관리자만? 접근하도록 해야 할것 같네요 ㅋㅋㅋ
		termService.addTerm(termName, isRequired, term);
		return "success";
	}

	@DeleteMapping(value = "/{termName}")
	String deleteTerm(@PathVariable String termName) { // 나중에 요 API도 관리자만 접근하도록 해야 할것 같네요 ㅋㅋㅋ
		if (termService.deleteTerm(termName)) {
			return "success";
		}
		return "fail";
	}

}
