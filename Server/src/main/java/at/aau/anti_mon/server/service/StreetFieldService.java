package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.dao.StreetFieldDAO;
import at.aau.anti_mon.server.entities.StreetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreetFieldService {

    private final StreetFieldDAO streetFieldDAO;

    @Autowired
    public StreetFieldService(StreetFieldDAO streetFieldDAO) {
        this.streetFieldDAO = streetFieldDAO;
    }

    public List<StreetField> getAllStreetFields() {
        return streetFieldDAO.findAll();
    }

    public StreetField getGameFieldByID(Integer id) {
        return streetFieldDAO.findById(id).orElse(null);
    }

    public StreetField createGameField(StreetField streetField) {
        return streetFieldDAO.save(streetField);
    }

    public StreetField updateGameField(Integer id, StreetField streetField) {
        StreetField existingStreetField = streetFieldDAO.findById(id).orElse(null);
        if (existingStreetField != null) {
            existingStreetField.setOwner(streetField.getOwner());
            return streetFieldDAO.save(existingStreetField);
        } else {
            return null;
        }
    }
}
