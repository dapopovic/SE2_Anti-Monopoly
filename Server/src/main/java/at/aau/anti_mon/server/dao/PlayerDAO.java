package at.aau.anti_mon.server.dao;

import at.aau.anti_mon.server.entities.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerDAO extends JpaRepository<PlayerEntity,Integer> {
    Optional<PlayerEntity> findByName(String name);
}
