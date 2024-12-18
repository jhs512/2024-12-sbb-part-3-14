package baekgwa.sbb.domain.user.service;

import baekgwa.sbb.domain.user.dto.UserDto.MypageInfo;

public interface UserService {

    void create(String username, String email, String password);

    MypageInfo getUserInfo(String loginUsername, Integer page, Integer size);
}
