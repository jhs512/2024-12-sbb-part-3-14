package com.programmers.recommend;

import com.programmers.article.Article;
import com.programmers.exception.AlreadyRecommendedException;
import com.programmers.exception.NotFoundDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final RecommendQuerydsl recommendQuerydsl;

    public void recommendQuestion(String username, Long questionId){
        if (recommendQuerydsl.existsRecommendByQuestionId(username, questionId)) {
            log.error("already exists recommend question");
            throw new AlreadyRecommendedException();
        }else {
            Article article = recommendQuerydsl.findArticleByQuestionId(questionId)
                    .orElseThrow(() -> new NotFoundDataException("Article"));
            recommendQuerydsl.saveQuestionRecommend(username, questionId);
        }
        //questionId question 찾아오고 거기서 getArticle..
    }

    public void recommendAnswer(Long answerId, String username){

    }
}
