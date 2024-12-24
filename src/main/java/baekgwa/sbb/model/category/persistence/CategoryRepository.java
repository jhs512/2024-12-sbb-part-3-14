package baekgwa.sbb.model.category.persistence;

import baekgwa.sbb.model.category.entity.Category;
import baekgwa.sbb.model.category.entity.CategoryType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryType(CategoryType categoryType);
}
