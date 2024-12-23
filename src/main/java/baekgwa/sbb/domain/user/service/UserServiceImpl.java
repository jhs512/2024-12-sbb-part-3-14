package baekgwa.sbb.domain.user.service;

import baekgwa.sbb.domain.user.dto.UserDto;
import baekgwa.sbb.domain.user.dto.UserDto.MypageInfo;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.global.util.EMailSender;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.redis.RedisRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;
    private final EMailSender eMailSender;
    private final RedisRepository redisRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

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

    @Transactional
    @Retryable(maxAttempts = 3, value = MailException.class, backoff = @Backoff(delay = 2000))
    @Override
    public void temporaryPassword(String email) throws MessagingException {
        SiteUser siteUser = userRepository.findByEmail(email).orElseThrow(
                () -> new DataNotFoundException("user not found"));
        String temporaryPassword = generateTemporaryPassword();
        String subject = "임시 비밀번호 안내";
        String content = "귀하의 임시 비밀번호는: " + temporaryPassword + "입니다. 로그인 후 비밀번호를 변경해주세요.";

        try {
            eMailSender.sendEmail(email, subject, content);
        } catch (Exception e) {
            throw e;
        }

        saveTemporaryPassword(siteUser.getUsername(), passwordEncoder.encode(temporaryPassword));
    }

    @Transactional
    @Override
    public void modifyPassword(String password, String name) {
        SiteUser findData = userRepository.findByUsername(name).orElseThrow(
                () -> new DataNotFoundException("user data not found"));
        findData.updateUserPassword(passwordEncoder.encode(password));
    }

    private void saveTemporaryPassword(String username, String tempPassword) {
        //todo : Magic Number 수정 필요.
        redisRepository.save(username, tempPassword, 5L, TimeUnit.MINUTES);
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}
