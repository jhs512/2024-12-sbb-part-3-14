package com.programmers.question;


import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question createQuestion(QuestionRegisterRequestDto requestDto) {
        return questionRepository.save(
                Question.builder()
                        .subject(requestDto.subject())
                        .content(requestDto.content())
                        .build()
        );
    }

    public Page<Question> findAllQuestions(PageRequestDto requestDto) {
        return questionRepository.findAll(PageRequest.of(requestDto.page(), requestDto.size()));
    }
}
