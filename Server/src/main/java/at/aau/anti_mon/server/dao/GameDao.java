package at.aau.anti_mon.server.dao;

import at.aau.anti_mon.server.entities.Game;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameDao extends JpaRepository<Game, Integer> {

    @Override
    @Query("SELECT DISTINCT m FROM Game m")
    @NotNull
    List<Game> findAll();

}
