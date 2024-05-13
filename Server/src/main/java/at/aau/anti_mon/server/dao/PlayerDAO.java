package at.aau.anti_mon.server.dao;

import at.aau.anti_mon.server.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerDAO extends JpaRepository<Player,Integer> {
    Optional<Player> findByName(String name);
}
