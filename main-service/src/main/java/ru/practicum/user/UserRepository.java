package ru.practicum.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE id IN :ids ORDER BY id LIMIT :size OFFSET :from", nativeQuery = true)
    List<User> getUsers(
            @Param("ids") List<Long> ids,
            @Param("from") int from,
            @Param("size") int size);

    List<User> findAllByOrderByIdAsc();

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    List<User> findByIdIn(List<Long> ids, Pageable pageable);
}