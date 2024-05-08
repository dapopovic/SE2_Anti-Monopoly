package at.aau.anti_mon.server.service;


import at.aau.anti_mon.server.dao.PlayerDAO;
import at.aau.anti_mon.server.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Player updatePlayer(Integer id, Player player) {
        Player existingPlayer = playerDAO.findById(id).orElse(null);
        if (existingPlayer != null) {
            existingPlayer.setName(player.getName());
            existingPlayer.setBalance(player.getBalance());
            return playerDAO.save(existingPlayer);
        } else {
            return null;
        }
    }

    public void deletePlayer(Integer id) {
        playerDAO.deleteById(id);
    }
}
