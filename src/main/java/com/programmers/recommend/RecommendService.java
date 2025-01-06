package com.programmers.recommend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendRepository recommendRepository;

    public void recommendQuestion(Long questionId, String username){

    }

    public void recommendAnswer(Long answerId, String username){

    }
}
