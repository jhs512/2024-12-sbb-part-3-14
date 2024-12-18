package baekgwa.sbb.domain.user.service;

import baekgwa.sbb.domain.user.dto.UserDto;
import baekgwa.sbb.domain.user.dto.UserDto.MypageInfo;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void create(String username, String email, String password) {
        SiteUser siteUser = SiteUser
                .builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(siteUser);
    }

    @Transactional(readOnly = true)
    @Override
    public MypageInfo getUserInfo(String loginUsername, Integer page, Integer size) {
        SiteUser user = userRepository.findByUsername(loginUsername).orElseThrow(
                () -> new DataNotFoundException("user data not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        Page<Question> questions = questionRepository.findAllBySiteUser(user, pageable);

        return MypageInfo
                .builder()
                .username(user.getUsername())
                .userIntroduction("아직 내 설명을 추가하는 기능은 없음")
                .email(user.getEmail())
                .questionInfos(questions.map(q -> UserDto.QuestionInfo
                                        .builder()
                                        .id(q.getId())
                                        .subject(q.getSubject())
                                        .content(q.getContent())
                                        .createDate(q.getCreateDate())
                                        .modifyDate(q.getModifyDate())
                                        .build()))
                .build();
    }
}
