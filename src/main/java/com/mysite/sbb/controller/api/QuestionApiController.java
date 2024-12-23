package com.mysite.sbb.controller.api;

import com.mysite.sbb.domain.dto.QuestionRequestDTO;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Question Controller", description = "질문 컨트롤러")
@RequestMapping("/api/v1/question")
@RequiredArgsConstructor
@RestController
public class QuestionApiController {

    private final QuestionServiceImpl questionServiceImpl;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createNewQuestion(
            @Valid @RequestBody QuestionRequestDTO questionRequestDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(bindingResult.getAllErrors());
        }
        questionServiceImpl.create(questionRequestDTO, principal.getName());
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/question/list")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @Valid @RequestBody QuestionRequestDTO questionRequestDTO,
            BindingResult bindingResult,
            Principal principal,
            @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(bindingResult.getAllErrors());
        }
        questionServiceImpl.modify(id, questionRequestDTO, principal.getName());
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, String.format("/question/detail/%s", id))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeQuestion(
            @PathVariable("id") Integer id,
            Principal principal) {
        this.questionServiceImpl.delete(id, principal.getName());
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/vote")
    public ResponseEntity<?> addVoteToQuestion(@PathVariable("id") Integer id, Principal principal) {
        this.questionServiceImpl.vote(id, principal.getName());
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }

}
