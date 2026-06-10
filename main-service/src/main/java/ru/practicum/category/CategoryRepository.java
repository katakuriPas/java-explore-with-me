package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    List<Category> findAllByOrderByIdAsc();

    @Query(value = "SELECT * FROM categories ORDER BY id LIMIT :size OFFSET :from", nativeQuery = true)
    List<Category> findCategoriesFromAndSize(
            @Param("from") Long from,
            @Param("size") Long size
    );
}
