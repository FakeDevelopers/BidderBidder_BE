package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

  FileEntity findByFileId(long fileId);
}
