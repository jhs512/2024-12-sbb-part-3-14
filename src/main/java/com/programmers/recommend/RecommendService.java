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
    private final RecommendQuerydsl recommendQuerydsl;

    public void recommendQuestion(String username, Long questionId){
        if (recommendQuerydsl.existsRecommendByQuestionId(username, questionId)) {
            throw new AlreadyRecommendedException();
        }else {
            recommendQuerydsl.saveQuestionRecommend(username, questionId);
        }
    }

    public void recommendAnswer(String username, Long answerId){
        if (recommendQuerydsl.existsRecommendByAnswerId(username, answerId)) {
            throw new AlreadyRecommendedException();
        }else {
            recommendQuerydsl.saveAnswerRecommend(username, answerId);
        }
    }

    public void recommendComment(String username, Long commentId){
        if (recommendQuerydsl.existsRecommendByCommentId(username, commentId)) {
            throw new AlreadyRecommendedException();
        }else {
            recommendQuerydsl.saveCommentRecommend(username, commentId);
        }
    }
}
