package com.fakedevelopers.ddangddangmarket.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    /*
        * Spring에서 제공하는 Security 기능을 사용하기 위해서는 UserDetails Service를 상속받아 사용해야합니다.*

        User 는 크게 3가지로 분류될 수 있습니다.
        1. 경매자
        2. 매도자
        3. 관리자 및 제3의 인물

        역할에 따른 서비스의 세부적인 구현은 UserService를 implement하여 사용합니다.
     */

}
