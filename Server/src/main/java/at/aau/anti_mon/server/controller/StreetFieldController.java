package at.aau.anti_mon.server.controller;

import at.aau.anti_mon.server.entities.StreetField;
import at.aau.anti_mon.server.service.StreetFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class StreetFieldController {

    private final StreetFieldService streetFieldService;

    @Autowired
    StreetFieldController(StreetFieldService streetFieldService) {
        this.streetFieldService = streetFieldService;
    }

    @MessageMapping("/streetfields")
    @SendTo("/topic/streetfield")
    public StreetField loadStreetField(Integer id) {
        return streetFieldService.getGameFieldByID(id);
    }



}
