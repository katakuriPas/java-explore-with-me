package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users ORDER BY id LIMIT :size OFFSET :from", nativeQuery = true)
    List<User> getUsers(
            @Param("from") Long from,
            @Param("size") Long size);

    List<User> findAllByOrderByIdAsc();

    boolean existsByEmail(String email);
}
