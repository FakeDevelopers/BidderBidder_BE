package com.fakedevelopers.bidderbidder.repository;

import com.fakedevelopers.bidderbidder.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepository: {email, nickname, password} 로 구성된 유저 정보에 대한 CRUD.
 * <br>
 * 초기 닉네임은 미정(#33 기준)
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByUsername(String username);
}
