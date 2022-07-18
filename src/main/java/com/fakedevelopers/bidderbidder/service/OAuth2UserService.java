package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.dto.UserRegisterDto;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OAuth2UserService implements UserDetailsService {
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findById(username).isPresent())
            return userRepository.findById(username).get();
        return null;
    }

    @Transactional
    public UserEntity register(OAuth2UserRegisterDto dto) {
        UserEntity userEntity  = UserEntity.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build();
        userRepository.save(userEntity);
        return userEntity;
    }

}
