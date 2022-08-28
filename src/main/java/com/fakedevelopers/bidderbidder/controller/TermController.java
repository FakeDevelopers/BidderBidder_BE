package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.service.TermService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping("/{termName}")
	String getTerm(@PathVariable String termName) {
		return termService.getTerm(termName);
	}

	@PostMapping(value = "/{termName}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	String addTerm(@PathVariable String termName, @RequestPart MultipartFile term)
			throws Exception { // 나중에 요 API는 관리자만? 접근하도록 해야 할것 같네요 ㅋㅋㅋ
		termService.addTerm(termName, term);
		return "success";
	}
}
