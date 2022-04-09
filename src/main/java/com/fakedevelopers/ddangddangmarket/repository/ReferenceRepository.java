package com.fakedevelopers.ddangddangmarket.repository;

import com.fakedevelopers.ddangddangmarket.entity.ReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceEntity, Long> {
}
