package com.fakedevelopers.ddangddangmarket.service;

import com.fakedevelopers.ddangddangmarket.dto.ReferenceDto;
import com.fakedevelopers.ddangddangmarket.entity.ReferenceEntity;
import com.fakedevelopers.ddangddangmarket.repository.ReferenceRepository;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReferenceService {
    private final static String RESOURCE_PATH = "/resources/upload";
    private final ReferenceRepository referenceRepository;
    ServletContext servletContext;

    ReferenceService(ReferenceRepository referenceRepository, ServletContext servletContext) {
        this.referenceRepository = referenceRepository;
        this.servletContext = servletContext;
    }

    public void saveReference(ReferenceDto referenceDto) throws IOException {
        String path = createPathIfNeeded();
        ReferenceEntity referenceEntity = new ReferenceEntity(path, referenceDto);

        referenceRepository.saveAndFlush(referenceEntity);
    }

    private String createPathIfNeeded() {
        String realPath = servletContext.getRealPath(RESOURCE_PATH);
        String today = new SimpleDateFormat("yyMMdd").format(new Date()); // SimpleDateFormat 잘못쓰면 큰일나요! 다른 분들은 이거쓰지마세요ㅋㅋㅋ
        String path = realPath + File.separator + today;

        File folder = new File(path);

        if (!folder.exists())
            folder.mkdirs();
        return path;
    }

    public List<ReferenceEntity> getAllReferences() {
        return referenceRepository.findAll();
    }
}
