package at.aau.anti_mon.server.dao;

import at.aau.anti_mon.server.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerDAO extends JpaRepository<Player,Integer> {
    List<Player> findByName(String name);
}
