package com.example.article_site.repository;

import com.example.article_site.domain.Author;
import com.example.article_site.domain.Category;
import com.example.article_site.domain.Question;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.example.article_site.domain.Author.createAuthor;
import static com.example.article_site.domain.Question.createQuestion;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void createSample(){
        Author author = createAuthor(
                "username2",
                "email2",
                "pw2"
        );
        authorRepository.save(author);

        Category category = Category.createCategory("전체");
        categoryRepository.save(category);

        Question question1 = createQuestion(
                "subject1",
                "content1",
                category,
                author
        );
        Question question2 = createQuestion(
                "subject2",
                "content2",
                category,
                author
        );

        questionRepository.save(question1);
        questionRepository.save(question2);
    }

    @Test
    public void testJpa(){

    }

    @Test
    public void findAll(){
        List<Question> all = this.questionRepository.findAll();
        assertEquals(2, all.size());
        Question q = all.get(0);
        assertEquals("subject1", q.getSubject());
    }

    @Test
    public void findById(){
        Optional<Question> oq = this.questionRepository.findById(1L);
        if(oq.isPresent()) {
            Question q = oq.get();
            assertEquals("subject1", q.getSubject());
        }
    }

    @Test
    public void findBySubject(){
        Question q = this.questionRepository.findBySubject("subject1");
        assertEquals("content1", q.getContent());
    }
}