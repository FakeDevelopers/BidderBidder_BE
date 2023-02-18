package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.dto.UserLoginDto;
import com.fakedevelopers.bidderbidder.dto.UserRegisterDto;
import com.fakedevelopers.bidderbidder.exception.ExistedUserException;
import com.fakedevelopers.bidderbidder.exception.InvalidPasswordException;
import com.fakedevelopers.bidderbidder.message.response.UserInfo;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.fakedevelopers.bidderbidder.domain.Constants.INIT_NICKNAME;
import static com.fakedevelopers.bidderbidder.domain.Constants.MAX_USERNAME_SIZE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * <h1>Register user entity</h1>
     *
     * @param dto 회원가입에 필수적인 정보(email, nickname) <br> OAuth2 회원가입은 패스워드를 요구하지 않는다.
     * @return the user entity
     */
    @Transactional
    public UserEntity register(OAuth2UserRegisterDto dto) {
        UserEntity userEntity = dto.toUserEntity();

        userEntity = userRepository.save(userEntity);
        // nickname 필드의 postfix에 identifier 추가 (닉네임 중복 방지)
        if (dto.getNickname().startsWith(INIT_NICKNAME)) {
            initNickname(userEntity, dto.getNickname() + userEntity.getId());
        }
        initUsername(userEntity, userEntity.getUsername());
        userRepository.saveAndFlush(userEntity);
        return userEntity;
    }

    public static String makeUsernameWithPrefix(String prefix, String name) {
        String username = prefix + name;
        int maxLength = Math.min(username.length(), MAX_USERNAME_SIZE);
        return username.substring(0, maxLength - 1);
    }

    private void initNickname(@NotNull UserEntity user, String nickname) {
        user.setNickname(nickname);
    }

    private void initUsername(@NotNull UserEntity user, String username) {
        username = username.substring(0,
                Math.min(MAX_USERNAME_SIZE - 1, username.length()));
        user.setUsername(username);
    }

    public String register(UserRegisterDto dto) throws FirebaseAuthException {
        UserEntity userEntity = UserEntity.of(dto);
        Optional<UserEntity> account = userRepository.findByUsername(userEntity.getUsername());
        account.ifPresent(e -> {
            throw new ExistedUserException();
        });

        // 온프레미스 db에도 유저 정보를 저장
        UserEntity savedUser = userRepository.saveAndFlush(userEntity);
        CreateRequest createRequest = new CreateRequest();
        createRequest.setUid(savedUser.getUsername());
        createRequest.setEmail(savedUser.getEmail());
        createRequest.setDisplayName(savedUser.getNickname());
        createRequest.setPassword(savedUser.getPassword());

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserRecord userRecord = firebaseAuth.createUser(createRequest);
        return firebaseAuth.createCustomToken(userRecord.getUid());
    }

    public UserInfo getUser(String uid) {
        UserEntity foundUser = userRepository.findByUsername(uid).orElseThrow(
                () -> new UsernameNotFoundException("user not found!")
        );

        return new UserInfo(foundUser);
    }

    public UserInfo userLoginWithPassword(UserLoginDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword(); // 현재 단계에선 hash 처리를 고려하지 않음

        UserEntity target = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("존재하지 않는 회원")
        );
        if (checkPassword(target.getId(), password)) {
            return new UserInfo(target);
        } else {
            throw new InvalidPasswordException();
        }
    }

    private boolean checkPassword(Long id, String password) {
        String storedPassword = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원")).getPassword();
        return storedPassword.equals(password);
    }
}
