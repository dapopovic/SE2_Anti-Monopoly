package at.aau.anti_mon.server.dao;

import at.aau.anti_mon.server.entities.StreetField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreetFieldDAO extends JpaRepository<StreetField,Integer> {
}
