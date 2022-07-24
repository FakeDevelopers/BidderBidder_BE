package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
