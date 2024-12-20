package com.mysite.sbb.catrgory;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.qustion.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Integer> {
    Category findById(int placeId);
}
