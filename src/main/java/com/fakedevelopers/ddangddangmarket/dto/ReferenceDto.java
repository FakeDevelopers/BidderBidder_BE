package com.fakedevelopers.ddangddangmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReferenceDto {
    private final String foo;
    private final int bar;
    private List<MultipartFile> files;
}
