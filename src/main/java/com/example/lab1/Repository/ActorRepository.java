package com.example.lab1.Repository;

import com.example.lab1.Entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByMovieId(Long movieId);

    @Query("SELECT a FROM Actor a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<Actor> findByNameContainingIgnoreCase(@Param("namePart") String namePart);
}