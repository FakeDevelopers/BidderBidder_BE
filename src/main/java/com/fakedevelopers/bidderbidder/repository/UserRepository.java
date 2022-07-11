package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
