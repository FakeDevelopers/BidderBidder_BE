package com.fakedevelopers.ddangddangmarket.controller;

import com.fakedevelopers.ddangddangmarket.dto.ReferenceDto;
import com.fakedevelopers.ddangddangmarket.entity.ReferenceEntity;
import com.fakedevelopers.ddangddangmarket.service.ReferenceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController("/reference")
public class ReferenceController {
    private final ReferenceService referenceService;

    ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String addReference(ReferenceDto referenceDto) throws IOException {
        referenceService.saveReference(referenceDto);
        return "success";
    }

    @GetMapping("/getAll")
    List<ReferenceEntity> getAllReference() {
        return referenceService.getAllReferences();
    }
}
