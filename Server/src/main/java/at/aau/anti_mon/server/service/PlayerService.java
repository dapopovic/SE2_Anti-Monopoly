package at.aau.anti_mon.server.service;


import at.aau.anti_mon.server.dao.PlayerDAO;
import at.aau.anti_mon.server.entities.Player;
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

    public List<Player> getAllPlayer() {
        return playerDAO.findAll();
    }

    public Player getPlayerByID(Integer id) {
        return playerDAO.findById(id).orElse(null);
    }

    public Player createPlayer(Player player) {
        return playerDAO.save(player);
    }

    public Player updatePlayer(Integer ID, Player player) {
        Player existingPlayer = playerDAO.findById(ID).orElse(null);
        if (existingPlayer != null) {
            existingPlayer.setName(player.getName());
            existingPlayer.setBalance(player.getBalance());
            existingPlayer.setPosition(player.getPosition());
            existingPlayer.setInJail(player.isInJail());
            existingPlayer.setPlayerFigure(player.getPlayerFigure());
            existingPlayer.setPlayerRole(player.getPlayerRole());
            return playerDAO.save(existingPlayer);
        } else {
            Logger.debug("Player with name {} not found", ID);
            return null;
        }
    }

    public void deletePlayer(Integer id) {
        playerDAO.deleteById(id);
    }
}
