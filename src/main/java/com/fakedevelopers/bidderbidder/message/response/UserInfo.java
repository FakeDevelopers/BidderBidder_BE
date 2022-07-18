package com.fakedevelopers.bidderbidder.message.response;

import lombok.Data;

@Data
public class UserInfo {
    // Controller가 반환하는 정보, front에서 받아서 유저 정보 표시
    private String email;
    private String nickname;

    public UserInfo(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
