package at.aau.anti_mon.server.service;


import at.aau.anti_mon.server.dao.PlayerDAO;
import at.aau.anti_mon.server.entities.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerDAO playerDAO;

    @Autowired
    public PlayerService(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    public List<PlayerEntity> getAllPlayer() {
        return playerDAO.findAll();
    }

    public PlayerEntity getPlayerByID(Integer id) {
        return playerDAO.findById(id).orElse(null);
    }

    public PlayerEntity createPlayer(PlayerEntity playerEntity) {
        return playerDAO.save(playerEntity);
    }

    public PlayerEntity updatePlayer(Integer id, PlayerEntity playerEntity) {
        PlayerEntity existingPlayerEntityEntity = playerDAO.findById(id).orElse(null);
        if (existingPlayerEntityEntity != null) {
            existingPlayerEntityEntity.setName(playerEntity.getName());
            existingPlayerEntityEntity.setBalance(playerEntity.getBalance());
            existingPlayerEntityEntity.setPosition(playerEntity.getPosition());
            existingPlayerEntityEntity.setInJail(playerEntity.isInJail());
            existingPlayerEntityEntity.setPlayerFigure(playerEntity.getPlayerFigure());
            existingPlayerEntityEntity.setPlayerRole(playerEntity.getPlayerRole());
            return playerDAO.save(existingPlayerEntityEntity);
        } else {
            Logger.debug("Player with name {} not found", id);
            return null;
        }
    }

    public void deletePlayer(Integer id) {
        playerDAO.deleteById(id);
    }
}
