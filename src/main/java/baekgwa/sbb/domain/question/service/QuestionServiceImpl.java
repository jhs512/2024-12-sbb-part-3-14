package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Deprecated
    @Transactional(readOnly = true)
    @Override
    public List<QuestionDto.MainInfo> getList() {
        return questionRepository.findAll()
                .stream().map(
                        data -> QuestionDto.MainInfo
                                .builder()
                                .id(data.getId())
                                .subject(data.getSubject())
                                .createDate(data.getCreateDate())
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public QuestionDto.DetailInfo getQuestion(Integer id) {
        Question question = questionRepository.findByIdWithAnswers(id).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        return QuestionDto.DetailInfo
                .builder()
                .id(question.getId())
                .subject(question.getSubject())
                .answerList(question.getAnswerList())
                .content(question.getContent())
                .createDate(question.getCreateDate())
                .build();
    }

    @Transactional
    @Override
    public void create(String subject, String content) {
        questionRepository.save(
                Question.builder()
                        .subject(subject)
                        .content(content)
                        .createDate(LocalDateTime.now())
                        .build());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<QuestionDto.MainInfo> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        return questionRepository.findAll(pageable)
                .map(
                        question -> QuestionDto.MainInfo
                                .builder()
                                .id(question.getId())
                                .subject(question.getSubject())
                                .createDate(question.getCreateDate())
                                .answerList(question.getAnswerList())
                                .build()
                );
    }
}
