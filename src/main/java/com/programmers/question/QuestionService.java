package com.programmers.question;


import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    public Question createQuestion(QuestionRegisterRequestDto requestDto) {
        return questionRepository.save(
                Question.builder()
                        .subject(requestDto.subject())
                        .content(requestDto.content())
                        .build()
        );
    }

    public Page<Question> findAllQuestions(PageRequestDto requestDto) {
        Pageable pageable = PageableUtils.createPageable(requestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
        long totalCount = questionRepository.count();
//        System.out.printf("total count :%d, total Page  : %s current page : %d",totalCount, Math.ceil((double) totalCount / pageable.getPageSize()), pageable.getPageNumber() + 1);
        if (Math.ceil((double) totalCount / pageable.getPageSize()) < pageable.getPageNumber() + 1) {
            return Page.empty();
        }else{
            return questionRepository.findAll(
                    PageableUtils.createPageable(requestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED));
        }
    }
}
