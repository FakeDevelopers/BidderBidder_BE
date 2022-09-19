package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.service.TermService;
import com.fakedevelopers.bidderbidder.service.TermService.TermInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

  @GetMapping("/list")
  @ApiOperation(value = "약관 리스트", notes = "필수 약관과 선택약관을 반환하는 API입니다.")
  Map<String, List<TermInfo>> getTerms() {
    return termService.getTerms();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "약관 다운로드", notes = "특정약관을 받아오는 API입니다.")
  String getTerm(
      @ApiParam(value = "가져올 약관ID", required = true, name = "id")
      @PathVariable @Min(1) long id) {
    return termService.getTerm(id);
  }

	@PostMapping(value = "/{termName}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = "약관 등록", notes = "약관을 등록하는 API입니다.")
	String addTerm(
      @ApiParam(value = "등록할 약관이름", required = true, name = "termName")
      @PathVariable String termName,
      @ApiParam(value = "업로드할 약관파일", required = true, type = "file", name = "term")
      @NotNull @RequestPart MultipartFile term,
      @ApiParam(value = "필수약관인지 여부", required = true, name = "isRequired")
      @RequestParam boolean isRequired)
			throws Exception { // 나중에 요 API는 관리자만? 접근하도록 해야 할것 같네요 ㅋㅋㅋ
		termService.addTerm(termName, isRequired, term);
		return Constants.SUCCESS;
	}

  @DeleteMapping(value = "/{id}")
  @ApiOperation(value = "약관 삭제", notes = "약관을 삭제하는 API입니다.")
  String deleteTerm(
      @ApiParam(value = "삭제할 약관ID", required = true, name = "id")
      @PathVariable @Min(1) long id) { // 나중에 요 API도 관리자만 접근하도록 해야 할것 같네요 ㅋㅋㅋ
    termService.deleteTerm(id);
    return Constants.SUCCESS;
  }

}
