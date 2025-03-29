package com.example.lab1.Repository;

import com.example.lab1.Entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByMovieId(Long movieId);
}